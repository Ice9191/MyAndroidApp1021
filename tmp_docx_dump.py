
import os, zipfile, docx
from docx import Document
from docx.table import Table
from docx.text.paragraph import Paragraph

src = r"D:\Cache\xwechat_files\wxid_8hqhdb6yab4x22_974e\msg\file\2026-11\工器具库系统文档v2.docx"
outdir = r"C:\Users\s1340\Desktop\MyAndroidApp1021\tmp_docx_imgs"
os.makedirs(outdir, exist_ok=True)

d = Document(src)
blob = zipfile.ZipFile(src)
media = [n for n in blob.namelist() if n.startswith("word/media/")]
idx = {}
for i, name in enumerate(sorted(media)):
    idx[name] = "img%02d%s" % (i + 1, os.path.splitext(name)[1])
    with open(os.path.join(outdir, idx[name]), "wb") as f:
        f.write(blob.read(name))

print("IMAGES:", len(media))
body = d.element.body
out = []
pi = 0
for child in body.iterchildren():
    tag = child.tag.split('}')[-1]
    if tag == 'p':
        par = Paragraph(child, d)
        t = par.text.strip()
        rels = []
        for blip in child.iter():
            if blip.tag.endswith('}blip'):
                rid = blip.get('{http://schemas.openxmlformats.org/officeDocument/2006/relationships}embed')
                if rid and rid in d.part.rels:
                    tgt = d.part.rels[rid].target_part.partname
                    nm = str(tgt).replace('/word/', 'word/')
                    rels.append(idx.get(nm, nm))
        if not t and not rels:
            continue
        out.append("[P%d]%s%s" % (pi, t, ("  <<IMAGE " + ",".join(rels) + ">>") if rels else ""))
        pi += 1
    elif tag == 'tbl':
        tb = Table(child, d)
        out.append("[TABLE rows=%d]" % len(tb.rows))
        for r in tb.rows:
            out.append("  | " + " | ".join(" ".join(c.text.split()) for c in r.cells))
        pi += 1

print("BLOCKS:", len(out))
open(os.path.join(outdir, "text.txt"), "w", encoding="utf-8").write("\n".join(out))
