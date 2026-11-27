@Composable
fun Test(
    flowViewModel: PutawayListViewModel,
    viewModel: PutawayConfirmViewModel = hiltViewModel(),
    onBackClick: ()-> Unit = {},
    onHomeClick: ()-> Unit = {}
){
    val listState by flowViewModel.uiState.collectAsStateWithLifecycle()
}