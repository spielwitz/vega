package common;

import java.util.Hashtable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
   * This class was created with the Resource Bundle Utility from the resource file
   *
   *   VegaResources_en_US
   *
   * The resource file is maintained with the Eclipse-Plugin ResourceBundle Editor.
   */
public class VegaResources 
{
	private static Hashtable<String,String> symbolDict;;
	private static String languageCode;
	private static ResourceBundle messages;

	static {
		setLocale("en-US");
		symbolDict = new Hashtable<String,String>();
		fillSymbolDict();
	}

	public static void setLocale(String newLanguageCode){
		languageCode = newLanguageCode;
		String[] language = languageCode.split("-");
		Locale currentLocale = new Locale(language[0], language[1]);
		messages = ResourceBundle.getBundle("VegaResources", currentLocale);
	}

	public static String getLocale(){
		return languageCode;
	}

	private static void fillSymbolDict() {
		// Last used symbolic key: FA
		symbolDict.put("00","HighScoreListOnServer_00");
		symbolDict.put("01","HighScoreList_01");
		symbolDict.put("02","HighScoresNoEntries_02");
		symbolDict.put("03","HowMuchMoney_03");
		symbolDict.put("04","ImportGameFromClipboard_04");
		symbolDict.put("05","ImportMovesOfPlayer_05");
		symbolDict.put("06","IncreaseMoneyProduction_06");
		symbolDict.put("08","Info_08");
		symbolDict.put("09","InputDisabled_09");
		symbolDict.put("0A","Insert_0A");
		symbolDict.put("0B","InvalidInput_0B");
		symbolDict.put("0C","Language_0C");
		symbolDict.put("0D","LastActivity_0D");
		symbolDict.put("0E","LastMoveUndone_0E");
		symbolDict.put("0F","LastTimeEnteredMoves_0F");
		symbolDict.put("0G","LoadDemoGame1_0G");
		symbolDict.put("0H","LoadDemoGame2_0H");
		symbolDict.put("0I","LoadError_0I");
		symbolDict.put("0J","LoadGame_0J");
		symbolDict.put("0K","LoadLocalGame_0K");
		symbolDict.put("0L","LocalHighScoreList_0L");
		symbolDict.put("0M","LogLevelChanged_0M");
		symbolDict.put("0N","LogLevel_0N");
		symbolDict.put("0O","LogSize_0O");
		symbolDict.put("0P","MainMenu_0P");
		symbolDict.put("0Q","Manual_0Q");
		symbolDict.put("0R","MaxInYear_0R");
		symbolDict.put("0S","MaximumLoad_0S");
		symbolDict.put("0T","Menu_0T");
		symbolDict.put("0U","MessageFromSector_0U");
		symbolDict.put("0V","MessagesUnread_0V");
		symbolDict.put("0W","Messages_0W");
		symbolDict.put("0X","MessengerCharactersLeft_0X");
		symbolDict.put("0Y","MessengerRecipients_0Y");
		symbolDict.put("0Z","MessengerSend_0Z");
		symbolDict.put("10","MinesweeperCrashed_10");
		symbolDict.put("11","MinesweeperShort_11");
		symbolDict.put("12","MinesweeperTransfer_12");
		symbolDict.put("13","Minesweeper_13");
		symbolDict.put("14","Minesweepers_14");
		symbolDict.put("15","MissionOrTransfer_15");
		symbolDict.put("16","Mission_16");
		symbolDict.put("17","MoneyFreight_17");
		symbolDict.put("18","MoneyProductionInYear_18");
		symbolDict.put("19","MoneyProductionOfPlanets_19");
		symbolDict.put("1A","Messenger_1A");
		symbolDict.put("1B","MinBuild_1B");
		symbolDict.put("1C","MinInYear_1C");
		symbolDict.put("1D","Mine100Plural_1D");
		symbolDict.put("1E","Mine100Short_1E");
		symbolDict.put("1F","Mine100_1F");
		symbolDict.put("1G","Mine250Plural_1G");
		symbolDict.put("1H","Mine250Short_1H");
		symbolDict.put("1I","Mine250_1I");
		symbolDict.put("1J","Mine500Plural_1J");
		symbolDict.put("1K","Mine500Short_1K");
		symbolDict.put("1L","Mine500_1L");
		symbolDict.put("1M","Mine50Plural_1M");
		symbolDict.put("1N","Mine50Short_1N");
		symbolDict.put("1O","Mine50_1O");
		symbolDict.put("1P","MineFieldSwept_1P");
		symbolDict.put("1Q","MinePlanted_1Q");
		symbolDict.put("1R","Mine_1R");
		symbolDict.put("1S","Minelayer100_1S");
		symbolDict.put("1T","Minelayer250_1T");
		symbolDict.put("1U","Minelayer500_1U");
		symbolDict.put("1V","Minelayer50_1V");
		symbolDict.put("1W","MinelayerArrived_1W");
		symbolDict.put("1X","MinelayerCrashed_1X");
		symbolDict.put("1Y","MinenraeumerPlural_1Y");
		symbolDict.put("1Z","MinesweeperArrived_1Z");
		symbolDict.put("20","MoneyProductionShort_20");
		symbolDict.put("21","MoneyProduction_21");
		symbolDict.put("22","MoneySupplyShort_22");
		symbolDict.put("23","MoneySupply_23");
		symbolDict.put("24","Money_24");
		symbolDict.put("25","More_25");
		symbolDict.put("26","MoveEntered_26");
		symbolDict.put("27","MovesDoNotBelongToThisYear_27");
		symbolDict.put("28","MovesNotImported_28");
		symbolDict.put("29","MovesNotTransmittedToServer_29");
		symbolDict.put("2A","MovesOfEmailPlayersNotYetImported_2A");
		symbolDict.put("2B","MovesSuccessfullyImported_2B");
		symbolDict.put("2C","MovesTransmittedToServer_2C");
		symbolDict.put("2D","InitialScreenNotReceived_2D");
		symbolDict.put("2E","MyName_2E");
		symbolDict.put("2F","NameOfGame_2F");
		symbolDict.put("2G","Name_2G");
		symbolDict.put("2H","NeutralFleet_2H");
		symbolDict.put("2I","Neutral_2I");
		symbolDict.put("2J","NewGameBoard_2J");
		symbolDict.put("2K","NewGamel_2K");
		symbolDict.put("2L","NewLanguageEffectiveAfterRestart_2L");
		symbolDict.put("2M","NewLocalGame_2M");
		symbolDict.put("2N","NewUserEmailBody_2N");
		symbolDict.put("2O","NextEventAnimated_2O");
		symbolDict.put("2P","NextEvent_2P");
		symbolDict.put("2Q","Next_2Q");
		symbolDict.put("2R","NoAllianceOnPlanet_2R");
		symbolDict.put("2S","NoAlliance_2S");
		symbolDict.put("2T","ConnectionErrorSecurityCode_2T");
		symbolDict.put("2U","Credentials_2U");
		symbolDict.put("2V","NoPlanetsWithAlliances_2V");
		symbolDict.put("2W","NoSpaceships_2W");
		symbolDict.put("2X","No_2X");
		symbolDict.put("2Y","NotConnected_2Y");
		symbolDict.put("2Z","NotEnoughBattleships_2Z");
		symbolDict.put("30","OK_30");
		symbolDict.put("31","Off_31");
		symbolDict.put("32","On_32");
		symbolDict.put("33","OpenBrowserError_33");
		symbolDict.put("34","OpenOutputWindow_34");
		symbolDict.put("35","OpenPdfViewerQuestion_35");
		symbolDict.put("36","OtherKey_36");
		symbolDict.put("37","OwnerShort_37");
		symbolDict.put("38","Page_38");
		symbolDict.put("39","Password_39");
		symbolDict.put("3A","PasswordsNotEqual_3A");
		symbolDict.put("3B","PasteClipboardHere_3B");
		symbolDict.put("3C","PatrolArrived_3C");
		symbolDict.put("3D","PatrolCapturedBattleships_3D");
		symbolDict.put("3E","PatrolCapturedMinelayer_3E");
		symbolDict.put("3F","PatrolCapturedMinesweeper_3F");
		symbolDict.put("3G","PatrolCapturedPatrol_3G");
		symbolDict.put("3H","PatrolCapturedSpy_3H");
		symbolDict.put("3I","PatrolCapturedTransporter_3I");
		symbolDict.put("3J","PatrolCrashed_3J");
		symbolDict.put("3K","PatrolShort_3K");
		symbolDict.put("3L","PatrolTransfer_3L");
		symbolDict.put("3M","Patrol_3M");
		symbolDict.put("3N","Patrols_3N");
		symbolDict.put("3O","PatrouillePlural_3O");
		symbolDict.put("3P","PdfOpenError_3P");
		symbolDict.put("3Q","PdfOpened_3Q");
		symbolDict.put("3R","PhysicalInventoryShort_3R");
		symbolDict.put("3S","PhysicalInventory_3S");
		symbolDict.put("3T","PlanetConquered_3T");
		symbolDict.put("3U","PlanetIsAttacked_3U");
		symbolDict.put("3V","PlanetShort_3V");
		symbolDict.put("3W","Planet_3W");
		symbolDict.put("3X","Planet_3X");
		symbolDict.put("3Y","PlanetsAlliancesSpies_3Y");
		symbolDict.put("3Z","PlanetsInYear_3Z");
		symbolDict.put("40","PlanetsProducing_40");
		symbolDict.put("41","Planets_41");
		symbolDict.put("42","PlayerCapitulated_42");
		symbolDict.put("43","PlayerEnteringMovesInputDisabled_43");
		symbolDict.put("44","PlayerGameOver_44");
		symbolDict.put("45","Player_45");
		symbolDict.put("47","PlayersAreWaiting_47");
		symbolDict.put("48","PlayersWaitingForInput_48");
		symbolDict.put("49","Players_49");
		symbolDict.put("4A","Points2_4A");
		symbolDict.put("4B","PointsInYear_4B");
		symbolDict.put("4C","Points_4C");
		symbolDict.put("4D","ProductionOfBattleships_4D");
		symbolDict.put("4E","PublishGameQuestion_4E");
		symbolDict.put("4F","PublishGame_4F");
		symbolDict.put("4G","QuitVegaDisplayQuestion_4G");
		symbolDict.put("4H","QuitVegaDisplay_4H");
		symbolDict.put("4I","QuitVega_4I");
		symbolDict.put("4J","Random_4J");
		symbolDict.put("4K","ReadConfiguration_4K");
		symbolDict.put("4L","RecentlyUsedEmailAddresses_4L");
		symbolDict.put("4N","Refresh_4N");
		symbolDict.put("4O","ReleaseFormatted_4O");
		symbolDict.put("4P","ServerAdministration_4P");
		symbolDict.put("4Q","RenewCredentials_4Q");
		symbolDict.put("4R","RenewUserCredentialsQuestion_4R");
		symbolDict.put("4S","RepeatPassword_4S");
		symbolDict.put("4T","ReplayEvaluation_4T");
		symbolDict.put("4U","ReplayOfEvaluationOfYear_4U");
		symbolDict.put("4V","Replay_4V");
		symbolDict.put("4W","RunningSince_4W");
		symbolDict.put("4X","SaveFileSuccess_4X");
		symbolDict.put("4Y","SaveGame_4Y");
		symbolDict.put("4Z","SaveLocalGameAs_4Z");
		symbolDict.put("50","SaveLogFileError_50");
		symbolDict.put("51","SaveLogFile_51");
		symbolDict.put("52","UnsavedChanges_52");
		symbolDict.put("53","MaximumConnections_53");
		symbolDict.put("54","SecurityCode_54");
		symbolDict.put("55","Select_55");
		symbolDict.put("56","SellPrice_56");
		symbolDict.put("57","Sell_57");
		symbolDict.put("58","SendActivationDataQuestion_58");
		symbolDict.put("59","SendEmailToGameHost_59");
		symbolDict.put("5A","SendEmailToPlayers_5A");
		symbolDict.put("5B","SendGameToAllPlayers_5B");
		symbolDict.put("5C","ServerBuild_5C");
		symbolDict.put("5D","UnsavedChangesSave_5D");
		symbolDict.put("5E","ServerCredentialsNotEntered_5E");
		symbolDict.put("5F","ServerIp_5F");
		symbolDict.put("5G","ServerLogEmpty_5G");
		symbolDict.put("5I","ServerPort_5I");
		symbolDict.put("5J","ServerSetupAborted_5J");
		symbolDict.put("5K","ServerShutdownSuccessfully_5K");
		symbolDict.put("5L","ServerStatus_5L");
		symbolDict.put("5M","ServerUrl_5M");
		symbolDict.put("5N","Settings_5N");
		symbolDict.put("5O","ShipsLaunched_5O");
		symbolDict.put("5P","ShutdownServerQuestion_5P");
		symbolDict.put("5Q","ShutdownServer_5Q");
		symbolDict.put("5R","Spaceships_5R");
		symbolDict.put("5S","Spies_5S");
		symbolDict.put("5T","SpyArrived_5T");
		symbolDict.put("5U","SpyCrashed_5U");
		symbolDict.put("5V","SpyDropped_5V");
		symbolDict.put("5W","SpyShort_5W");
		symbolDict.put("5X","SpyTransfer_5X");
		symbolDict.put("5Y","Spy_5Y");
		symbolDict.put("5Z","StartPlanet_5Z");
		symbolDict.put("60","StartTutorial_60");
		symbolDict.put("61","Start_61");
		symbolDict.put("62","Start_62");
		symbolDict.put("63","Statistics_63");
		symbolDict.put("64","SubmitChangesToServer_64");
		symbolDict.put("65","Success_65");
		symbolDict.put("66","Terminalserver_66");
		symbolDict.put("67","TerminateAlliance_67");
		symbolDict.put("68","TextFile_68");
		symbolDict.put("69","ThereAreNoMoves_69");
		symbolDict.put("6A","ThisIsTheStartPlanet_6A");
		symbolDict.put("6B","Timeout_6B");
		symbolDict.put("6C","ToShort_6C");
		symbolDict.put("6D","ToWhichPlanetBattleships_6D");
		symbolDict.put("6E","ToWhichPlanetMine100_6E");
		symbolDict.put("6F","ToWhichPlanetMine250_6F");
		symbolDict.put("6G","ToWhichPlanetMine500_6G");
		symbolDict.put("6H","ToWhichPlanetMine50_6H");
		symbolDict.put("6I","ToWhichPlanetMinesweeper_6I");
		symbolDict.put("6J","ToWhichPlanetPatrol_6J");
		symbolDict.put("6K","ToWhichPlanetSpy_6K");
		symbolDict.put("6L","ToWhichPlanetTransporter_6L");
		symbolDict.put("6M","Transfer_6M");
		symbolDict.put("6N","TransporterArrived_6N");
		symbolDict.put("6O","TransporterCrashed_6O");
		symbolDict.put("6P","TransporterPlural_6P");
		symbolDict.put("6Q","TransporterShort_6Q");
		symbolDict.put("6R","Transporter_6R");
		symbolDict.put("6S","Transporters_6S");
		symbolDict.put("6T","TryAgain_6T");
		symbolDict.put("6U","TutorialActionNotAllowed_6U");
		symbolDict.put("6V","TutorialActionNotExpected_6V");
		symbolDict.put("6W","TutorialAndDemoGames_6W");
		symbolDict.put("6X","TutorialButtonNext_6X");
		symbolDict.put("6Y","TutorialStartTitle_6Y");
		symbolDict.put("6Z","TutorialStart_6Z");
		symbolDict.put("70","TutorialText00_70");
		symbolDict.put("71","TutorialText01_71");
		symbolDict.put("72","TutorialText02_72");
		symbolDict.put("73","TutorialText03_73");
		symbolDict.put("74","TutorialText04_74");
		symbolDict.put("75","TutorialText05_75");
		symbolDict.put("76","TutorialText06_76");
		symbolDict.put("77","TutorialText07_77");
		symbolDict.put("78","TutorialText08_78");
		symbolDict.put("79","TutorialText09_79");
		symbolDict.put("7A","TutorialText10_7A");
		symbolDict.put("7B","TutorialText11_7B");
		symbolDict.put("7C","TutorialText12_7C");
		symbolDict.put("7D","TutorialText13_7D");
		symbolDict.put("7E","TutorialText14_7E");
		symbolDict.put("7F","TutorialText15_7F");
		symbolDict.put("7G","TutorialText16_7G");
		symbolDict.put("7H","TutorialText18_7H");
		symbolDict.put("7I","TutorialText17_7I");
		symbolDict.put("7J","TutorialText19_7J");
		symbolDict.put("7K","TutorialText20_7K");
		symbolDict.put("7L","TutorialText21_7L");
		symbolDict.put("7M","TutorialTitle_7M");
		symbolDict.put("7N","TypeShort_7N");
		symbolDict.put("7O","Type_7O");
		symbolDict.put("7P","UndoLastMoveQuestion_7P");
		symbolDict.put("7Q","Undo_7Q");
		symbolDict.put("7R","Unknown_7R");
		symbolDict.put("7S","UpdateUserQuestion_7S");
		symbolDict.put("7U","UserActivationSuccess_7U");
		symbolDict.put("7V","UserDeleted_7V");
		symbolDict.put("7W","UserId_7W");
		symbolDict.put("7X","UserNameInvalid_7X");
		symbolDict.put("7Y","UserNotExists_7Y");
		symbolDict.put("7Z","UserNotParticipating_7Z");
		symbolDict.put("80","UserUpdated_80");
		symbolDict.put("81","Users_81");
		symbolDict.put("84","VegaDisplayServer_84");
		symbolDict.put("85","VegaDisplay_85");
		symbolDict.put("86","VegaDisplaysPassive_86");
		symbolDict.put("87","VegaLanguage_87");
		symbolDict.put("88","ConnectAsPlayerWithCredentials_88");
		symbolDict.put("89","VegaServerSetupWelcome_89");
		symbolDict.put("8A","VegaServer_8A");
		symbolDict.put("8B","Vega_8B");
		symbolDict.put("8C","WaitForEmailFromGameHost_8C");
		symbolDict.put("8D","WaitForEvaluation_8D");
		symbolDict.put("8E","WaitingForOtherPlayers_8E");
		symbolDict.put("8F","WebServerConfiguration_8F");
		symbolDict.put("8G","WebServerInactive_8G");
		symbolDict.put("8H","WebServerInventory_8H");
		symbolDict.put("8I","WebServerResourceNotFound_8I");
		symbolDict.put("8J","WebServer_8J");
		symbolDict.put("8K","WhichType_8K");
		symbolDict.put("8L","WhoIsMissing_8L");
		symbolDict.put("8M","WriteEmail_8M");
		symbolDict.put("8N","YearAlreadyEvaluated_8N");
		symbolDict.put("8O","YearBack_8O");
		symbolDict.put("8P","YearDayShort_8P");
		symbolDict.put("8Q","YearDay_8Q");
		symbolDict.put("8R","YearEndOfYear_8R");
		symbolDict.put("8S","YearEndShort_8S");
		symbolDict.put("8T","YearForward_8T");
		symbolDict.put("8U","YearOf_8U");
		symbolDict.put("8V","YearOf_8V");
		symbolDict.put("8W","Year_8W");
		symbolDict.put("8X","Years_8X");
		symbolDict.put("8Y","Yes_8Y");
		symbolDict.put("8Z","YouAreNotGameHost_8Z");
		symbolDict.put("90","YouAreNotOwnerOfPlanet_90");
		symbolDict.put("91","YouCannotStartMoreBattleships_91");
		symbolDict.put("92","YouCannotTransportThisAmountOfMoney_92");
		symbolDict.put("93","YouHaveNoPlanets_93");
		symbolDict.put("94","AbortReplay_94");
		symbolDict.put("95","AboutVega_95");
		symbolDict.put("96","AcceptChanges_96");
		symbolDict.put("97","ActionCancelled_97");
		symbolDict.put("98","ActionNotPossible_98");
		symbolDict.put("99","ActionNotPossible_99");
		symbolDict.put("9A","ActivateServer_9A");
		symbolDict.put("9B","ActivateUser_9B");
		symbolDict.put("9C","Activate_9C");
		symbolDict.put("9D","ActivationPasswordTooShort_9D");
		symbolDict.put("9E","ActivationPassword_9E");
		symbolDict.put("9F","AddToHighScoreListQuestion_9F");
		symbolDict.put("9G","AddressSeparator_9G");
		symbolDict.put("9H","AdminCredentials_9H");
		symbolDict.put("9I","ServerSettings_9I");
		symbolDict.put("9J","AgreeWithGameBoardQuestion_9J");
		symbolDict.put("9K","AllBattleships_9K");
		symbolDict.put("9L","AllEmailPlayerMovesImported_9L");
		symbolDict.put("9M","AllOff_9M");
		symbolDict.put("9N","AllOn_9N");
		symbolDict.put("9O","AllianceDefinitionError_9O");
		symbolDict.put("9P","AllianceOwnerNotIncluded2_9P");
		symbolDict.put("9Q","AllianceOwnerNotIncluded_9Q");
		symbolDict.put("9R","AllianceShort_9R");
		symbolDict.put("9S","AllianceStructureOnPlanet_9S");
		symbolDict.put("9T","Alliance_9T");
		symbolDict.put("9U","Alliances_9U");
		symbolDict.put("9V","AlliedBattleships_9V");
		symbolDict.put("9W","Allied_9W");
		symbolDict.put("9X","AreYouSure_9X");
		symbolDict.put("9Y","Arrival2_9Y");
		symbolDict.put("9Z","Arrival_9Z");
		symbolDict.put("A0","AssignUsersToAllPlayers_A0");
		symbolDict.put("A1","AttackFailed_A1");
		symbolDict.put("A3","Attributes_A3");
		symbolDict.put("A4","AuthenticationFile_A4");
		symbolDict.put("A5","AutomaticSave_A5");
		symbolDict.put("A6","Back_A6");
		symbolDict.put("A7","BattleshipProductionShort_A7");
		symbolDict.put("A8","BattleshipProduction_A8");
		symbolDict.put("A9","BattleshipsArrivedAtPlanet_A9");
		symbolDict.put("AA","BattleshipsInYear_AA");
		symbolDict.put("AB","BattleshipsKilledByMine2_AB");
		symbolDict.put("AC","BattleshipsKilledByMine_AC");
		symbolDict.put("AD","BattleshipsMustLeavePlanet_AD");
		symbolDict.put("AE","BattleshipsNotLaunchedFromPlanet_AE");
		symbolDict.put("AF","BattleshipsShort_AF");
		symbolDict.put("AG","BattleshipsWaiting_AG");
		symbolDict.put("AH","Battleships_AH");
		symbolDict.put("AI","BeginningOfYear_AI");
		symbolDict.put("AJ","BlackHoleDestroyedBattleships_AJ");
		symbolDict.put("AK","BlackHoleMine_AK");
		symbolDict.put("AL","BlackHoleMines_AL");
		symbolDict.put("AM","BlackHoleMinesweeper_AM");
		symbolDict.put("AN","BlackHolePatrol_AN");
		symbolDict.put("AO","BlackHoleSpy_AO");
		symbolDict.put("AP","BlackHoleTransport_AP");
		symbolDict.put("AQ","Board_AQ");
		symbolDict.put("AR","BuyPrice_AR");
		symbolDict.put("AS","BuySellDefensiveBattleships_AS");
		symbolDict.put("AT","Buy_AT");
		symbolDict.put("AU","Cancel_AU");
		symbolDict.put("AV","Capitulate_AV");
		symbolDict.put("AW","ChangeLogLevelQuestion_AW");
		symbolDict.put("AX","ChangeLogLevel_AX");
		symbolDict.put("AY","ChangeSelection_AY");
		symbolDict.put("AZ","ClientBuild_AZ");
		symbolDict.put("B0","ClientServerDifferentBuilds_B0");
		symbolDict.put("B1","ClipboardImportErrorPassword_B1");
		symbolDict.put("B2","ClipboardImportError_B2");
		symbolDict.put("B3","CloseStatistics_B3");
		symbolDict.put("B4","Close_B4");
		symbolDict.put("B5","Color_B5");
		symbolDict.put("B6","Commander_B6");
		symbolDict.put("B7","Connect_B7");
		symbolDict.put("B8","ConnectedVegaDisplayClients_B8");
		symbolDict.put("B9","ConnectedWithServer_B9");
		symbolDict.put("BA","ConnectedWithVegaServer_BA");
		symbolDict.put("BB","ConnectionError_BB");
		symbolDict.put("BC","ConnectionSettings_BC");
		symbolDict.put("BD","ConnectionStatus_BD");
		symbolDict.put("BE","ConnectionSuccessful_BE");
		symbolDict.put("BF","ConnectionTest_BF");
		symbolDict.put("BG","ConnectionToServerNotEstablished_BG");
		symbolDict.put("BH","CopiedToClipboard_BH");
		symbolDict.put("BI","CopyToClipboard_BI");
		symbolDict.put("BJ","Count_BJ");
		symbolDict.put("BK","CreateEmail_BK");
		symbolDict.put("BL","CreateNewUser_BL");
		symbolDict.put("BM","CreateUserQuestion_BM");
		symbolDict.put("BN","CurrentAllies_BN");
		symbolDict.put("BO","DateFormatted_BO");
		symbolDict.put("BP","DayOf_BP");
		symbolDict.put("BQ","Default_BQ");
		symbolDict.put("BR","DefensiveBattleshipsShort_BR");
		symbolDict.put("BS","DefensiveBattleships_BS");
		symbolDict.put("BT","DeleteGameQuestion_BT");
		symbolDict.put("BU","DeleteGame_BU");
		symbolDict.put("BV","DeleteUserQuestion_BV");
		symbolDict.put("BW","DeleteUser_BW");
		symbolDict.put("BX","Delete_BX");
		symbolDict.put("BY","DestinationOfTransferMustBePlanet_BY");
		symbolDict.put("BZ","DestinationPlanet_BZ");
		symbolDict.put("C0","DestinationSectorOrPlanet_C0");
		symbolDict.put("C1","DestinationShort_C1");
		symbolDict.put("C2","DisplayAllianceOnPlanet_C2");
		symbolDict.put("C3","DistanceMatrixHeader_C3");
		symbolDict.put("C4","DistanceMatrix_C4");
		symbolDict.put("C5","DoYouWantToQuitVega_C5");
		symbolDict.put("C6","DownloadLog_C6");
		symbolDict.put("C7","DuplicatePlayers_C7");
		symbolDict.put("C8","EmailActions_C8");
		symbolDict.put("C9","EmailAddressGameHostInvalid_C9");
		symbolDict.put("CA","EmailAddressInvalid_CA");
		symbolDict.put("CB","EmailAddressUnknown_CB");
		symbolDict.put("CC","EmailAddress_CC");
		symbolDict.put("CD","EmailAdmin_CD");
		symbolDict.put("CE","EmailBodyInvitation_CE");
		symbolDict.put("CF","EmailGameEmailBodyMoves_CF");
		symbolDict.put("CG","EmailGameEmailBody_CG");
		symbolDict.put("CH","EmailModeSettings_CH");
		symbolDict.put("CI","EmailMode_CI");
		symbolDict.put("CJ","EmailOpenError_CJ");
		symbolDict.put("CK","EmailSettings_CK");
		symbolDict.put("CL","EmailSubjectInvitation_CL");
		symbolDict.put("CM","EmailSubjectNewUser_CM");
		symbolDict.put("CN","EmailWasCreatedInStandardClient_CN");
		symbolDict.put("CO","Email_CO");
		symbolDict.put("CP","EmailsWereCreated_CP");
		symbolDict.put("CQ","EndOfYear_CQ");
		symbolDict.put("CR","EnterAllianceMembers_CR");
		symbolDict.put("CS","EnterMoves_CS");
		symbolDict.put("CT","EntriesCorrectQuestion_CT");
		symbolDict.put("CU","Error_CU");
		symbolDict.put("CV","EvaluationBegins_CV");
		symbolDict.put("CW","EvaluationEnd_CW");
		symbolDict.put("CX","Evaluation_CX");
		symbolDict.put("CY","FightSimulationAttackNoSuccess_CY");
		symbolDict.put("CZ","FightSimulationAttackSuccess_CZ");
		symbolDict.put("D0","FightSimulationAttackerCount_D0");
		symbolDict.put("D2","FightSimulationPlanetCount_D2");
		symbolDict.put("D4","FightSimulation_D4");
		symbolDict.put("D6","FileContainsInvalidCredentials_D6");
		symbolDict.put("D7","FileFilterDescription_D7");
		symbolDict.put("D8","FileNotExists_D8");
		symbolDict.put("D9","FileNotValid_D9");
		symbolDict.put("DB","FinalizeGameQuestion_DB");
		symbolDict.put("DC","FinalizeGameQuestion_DC");
		symbolDict.put("DD","FinalizeGame_DD");
		symbolDict.put("DE","Finalize_DE");
		symbolDict.put("DF","FinalizedGameInYear_DF");
		symbolDict.put("DG","FinalizedGameMoneyProduction_DG");
		symbolDict.put("DH","FinalizedGamePosition_DH");
		symbolDict.put("DI","FinalizedGameUnderMyServerBasedGames_DI");
		symbolDict.put("DJ","FinalizedGames_DJ");
		symbolDict.put("DK","FinishEnterMovesQuestion_DK");
		symbolDict.put("DL","FinishEnterMoves_DL");
		symbolDict.put("DM","Finish_DM");
		symbolDict.put("DN","ForeignSpies_DN");
		symbolDict.put("DO","Freight_DO");
		symbolDict.put("DP","FromShort_DP");
		symbolDict.put("DQ","GameCreatedSendMail_DQ");
		symbolDict.put("DR","GameCreated_DR");
		symbolDict.put("DS","GameDeletedSuccessfully_DS");
		symbolDict.put("DT","GameFinalizedSuccessfully_DT");
		symbolDict.put("DU","GameHasBeenFinalized_DU");
		symbolDict.put("DV","GameHost_DV");
		symbolDict.put("DW","GameInfo_DW");
		symbolDict.put("DX","GameNameInvalid_DX");
		symbolDict.put("DY","GameNotExists_DY");
		symbolDict.put("DZ","GameParameters_DZ");
		symbolDict.put("E0","GameStartedOn_E0");
		symbolDict.put("E1","GameWithSameNameExists_E1");
		symbolDict.put("E2","GamesOnServer_E2");
		symbolDict.put("E3","GamesOnVegaServer_E3");
		symbolDict.put("E4","GetIp_E4");
		symbolDict.put("E5","HideOrShowSpaceships_E5");
		symbolDict.put("E6","UserIsActive_E6");
		symbolDict.put("E7","CountShort_E7");
		symbolDict.put("E8","ActiveSpies_E8");
		symbolDict.put("E9","PlanetConqueredNeutral_E9");
		symbolDict.put("EA","LoadServerData_EA");
		symbolDict.put("EB","Bytes_EB");
		symbolDict.put("EC","InactiveUserFromClipboard_EC");
		symbolDict.put("ED","ActiveUserFromFile_ED");
		symbolDict.put("EE","AddCredentials_EE");
		symbolDict.put("EF","DeleteCredentials_EF");
		symbolDict.put("EG","DeleteCredentialsAYS_EG");
		symbolDict.put("EH","ServerCredentialsLocked_EH");
		symbolDict.put("EI","UnlockServerCredentials_EI");
		symbolDict.put("EJ","ChangeServerCredentialsPasswort_EJ");
		symbolDict.put("EK","SetServerCredentialsPasswort_EK");
		symbolDict.put("EL","OldPassword_EL");
		symbolDict.put("EM","NewPassword_EM");
		symbolDict.put("EN","NewPasswordRepeat_EN");
		symbolDict.put("EO","PasswordWrong_EO");
		symbolDict.put("EP","PasswordWrong2_EP");
		symbolDict.put("EQ","PasswordInvalid_EQ");
		symbolDict.put("ER","PasswordLength_ER");
		symbolDict.put("ES","ChangePassword_ES");
		symbolDict.put("ET","ClearServerCredentials_ET");
		symbolDict.put("EU","ClearServerCredentialsAys_EU");
		symbolDict.put("EV","ServerAdminCredentialsCreated_EV");
		symbolDict.put("EW","UnsentMessages_EW");
		symbolDict.put("EX","UnsentMessages2_EX");
		symbolDict.put("EY","ConversationNew_EY");
		symbolDict.put("EZ","ConversationDelete_EZ");
		symbolDict.put("F0","CredentialsTextFileInstructions_F0");
		symbolDict.put("F1","FightAttacker_F1");
		symbolDict.put("F2","FightDefender_F2");
		symbolDict.put("F3","TutorialText07a_F3");
		symbolDict.put("F5","CombatStrengthShort_F5");
		symbolDict.put("F6","TutorialText04a_F6");
		symbolDict.put("F7","TutorialText04b_F7");
		symbolDict.put("F8","VegaHelpError_F8");
		symbolDict.put("F9","FightSimulationCombatStrength_F9");
		symbolDict.put("FA","BuySellCombatStrength_FA");
	}
	public static String getString(String symbolString){
		StringBuilder sb = new StringBuilder();
		int pos = 0;

		do {
			int startPos = symbolString.indexOf("£", pos);
			if (startPos < 0){
				sb.append(symbolString.substring(pos, symbolString.length()));
				break;}
			sb.append(symbolString.substring(pos, startPos));
			int endPos = symbolString.indexOf("£", startPos + 1);
			String subString = symbolString.substring(startPos + 1, endPos);
			Object[] parts = subString.split("§");
			if (symbolDict.containsKey(parts[0])){
				if (parts.length == 1)
					sb.append(messages.getString(symbolDict.get(parts[0])));
				else{
					Object[] args = new Object[parts.length - 1];
					for (int i = 1; i < parts.length; i++)
						args[i-1] = parts[i];
						sb.append(MessageFormat.format(messages.getString(symbolDict.get(parts[0])) ,args));
			}}
			pos = endPos + 1;
		} while (true);
		return sb.toString();
	}

	/**
	   * Leaderboard on the VEGA Server [00]
	   */
	public static String HighScoreListOnServer(boolean symbol) {
		return symbol ? "£00£":messages.getString("HighScoreListOnServer_00");
	}

	/**
	   * Leaderboard [01]
	   */
	public static String HighScoreList(boolean symbol) {
		return symbol ? "£01£":messages.getString("HighScoreList_01");
	}

	/**
	   * The leaderboard is empty. [02]
	   */
	public static String HighScoresNoEntries(boolean symbol) {
		return symbol ? "£02£":messages.getString("HighScoresNoEntries_02");
	}

	/**
	   * How many $ (max. {0})? [03]
	   */
	public static String HowMuchMoney(boolean symbol, String arg0) {
		return symbol ? "£03§"+arg0+"£":MessageFormat.format(messages.getString("HowMuchMoney_03"), arg0);
	}

	/**
	   * Import e-mail game from clipboard [04]
	   */
	public static String ImportGameFromClipboard(boolean symbol) {
		return symbol ? "£04£":messages.getString("ImportGameFromClipboard_04");
	}

	/**
	   * Import moves of a player [05]
	   */
	public static String ImportMovesOfPlayer(boolean symbol) {
		return symbol ? "£05£":messages.getString("ImportMovesOfPlayer_05");
	}

	/**
	   * $ production/year (+{0}) [06]
	   */
	public static String IncreaseMoneyProduction(boolean symbol, String arg0) {
		return symbol ? "£06§"+arg0+"£":MessageFormat.format(messages.getString("IncreaseMoneyProduction_06"), arg0);
	}

	/**
	   * Info [08]
	   */
	public static String Info(boolean symbol) {
		return symbol ? "£08£":messages.getString("Info_08");
	}

	/**
	   * Input disabled [09]
	   */
	public static String InputDisabled(boolean symbol) {
		return symbol ? "£09£":messages.getString("InputDisabled_09");
	}

	/**
	   * Insert [0A]
	   */
	public static String Insert(boolean symbol) {
		return symbol ? "£0A£":messages.getString("Insert_0A");
	}

	/**
	   * Invalid input. [0B]
	   */
	public static String InvalidInput(boolean symbol) {
		return symbol ? "£0B£":messages.getString("InvalidInput_0B");
	}

	/**
	   * Language/Sprache [0C]
	   */
	public static String Language(boolean symbol) {
		return symbol ? "£0C£":messages.getString("Language_0C");
	}

	/**
	   * Last activity: {0} [0D]
	   */
	public static String LastActivity(boolean symbol, String arg0) {
		return symbol ? "£0D§"+arg0+"£":MessageFormat.format(messages.getString("LastActivity_0D"), arg0);
	}

	/**
	   * Last move undone. [0E]
	   */
	public static String LastMoveUndone(boolean symbol) {
		return symbol ? "£0E£":messages.getString("LastMoveUndone_0E");
	}

	/**
	   * This was the last time in this game that you entered your moves. [0F]
	   */
	public static String LastTimeEnteredMoves(boolean symbol) {
		return symbol ? "£0F£":messages.getString("LastTimeEnteredMoves_0F");
	}

	/**
	   * Load demo game 1 [0G]
	   */
	public static String LoadDemoGame1(boolean symbol) {
		return symbol ? "£0G£":messages.getString("LoadDemoGame1_0G");
	}

	/**
	   * Load demo game 2 [0H]
	   */
	public static String LoadDemoGame2(boolean symbol) {
		return symbol ? "£0H£":messages.getString("LoadDemoGame2_0H");
	}

	/**
	   * Load error [0I]
	   */
	public static String LoadError(boolean symbol) {
		return symbol ? "£0I£":messages.getString("LoadError_0I");
	}

	/**
	   * Load game [0J]
	   */
	public static String LoadGame(boolean symbol) {
		return symbol ? "£0J£":messages.getString("LoadGame_0J");
	}

	/**
	   * Load local game [0K]
	   */
	public static String LoadLocalGame(boolean symbol) {
		return symbol ? "£0K£":messages.getString("LoadLocalGame_0K");
	}

	/**
	   * Local leaderboard [0L]
	   */
	public static String LocalHighScoreList(boolean symbol) {
		return symbol ? "£0L£":messages.getString("LocalHighScoreList_0L");
	}

	/**
	   * The log level of the server was set successfully. [0M]
	   */
	public static String LogLevelChanged(boolean symbol) {
		return symbol ? "£0M£":messages.getString("LogLevelChanged_0M");
	}

	/**
	   * Log level [0N]
	   */
	public static String LogLevel(boolean symbol) {
		return symbol ? "£0N£":messages.getString("LogLevel_0N");
	}

	/**
	   * Log size [0O]
	   */
	public static String LogSize(boolean symbol) {
		return symbol ? "£0O£":messages.getString("LogSize_0O");
	}

	/**
	   * Main menu [0P]
	   */
	public static String MainMenu(boolean symbol) {
		return symbol ? "£0P£":messages.getString("MainMenu_0P");
	}

	/**
	   * Manual [0Q]
	   */
	public static String Manual(boolean symbol) {
		return symbol ? "£0Q£":messages.getString("Manual_0Q");
	}

	/**
	   * Max: {0} (year {1}) [0R]
	   */
	public static String MaxInYear(boolean symbol, String arg0, String arg1) {
		return symbol ? "£0R§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("MaxInYear_0R"), arg0, arg1);
	}

	/**
	   * Maximum load [0S]
	   */
	public static String MaximumLoad(boolean symbol) {
		return symbol ? "£0S£":messages.getString("MaximumLoad_0S");
	}

	/**
	   * Menu [0T]
	   */
	public static String Menu(boolean symbol) {
		return symbol ? "£0T£":messages.getString("Menu_0T");
	}

	/**
	   * {0}: Message from sector {1}: [0U]
	   */
	public static String MessageFromSector(boolean symbol, String arg0, String arg1) {
		return symbol ? "£0U§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("MessageFromSector_0U"), arg0, arg1);
	}

	/**
	   * You have unread messages [0V]
	   */
	public static String MessagesUnread(boolean symbol) {
		return symbol ? "£0V£":messages.getString("MessagesUnread_0V");
	}

	/**
	   * Messages [0W]
	   */
	public static String Messages(boolean symbol) {
		return symbol ? "£0W£":messages.getString("Messages_0W");
	}

	/**
	   * {0} character(s) left [0X]
	   */
	public static String MessengerCharactersLeft(boolean symbol, String arg0) {
		return symbol ? "£0X§"+arg0+"£":MessageFormat.format(messages.getString("MessengerCharactersLeft_0X"), arg0);
	}

	/**
	   * Recipients [0Y]
	   */
	public static String MessengerRecipients(boolean symbol) {
		return symbol ? "£0Y£":messages.getString("MessengerRecipients_0Y");
	}

	/**
	   * Send [0Z]
	   */
	public static String MessengerSend(boolean symbol) {
		return symbol ? "£0Z£":messages.getString("MessengerSend_0Z");
	}

	/**
	   * 1 minesweeper crashed on planet {0}. [10]
	   */
	public static String MinesweeperCrashed(boolean symbol, String arg0) {
		return symbol ? "£10§"+arg0+"£":MessageFormat.format(messages.getString("MinesweeperCrashed_10"), arg0);
	}

	/**
	   * Msw [11]
	   */
	public static String MinesweeperShort(boolean symbol) {
		return symbol ? "£11£":messages.getString("MinesweeperShort_11");
	}

	/**
	   * Minesweeper (transfer) [12]
	   */
	public static String MinesweeperTransfer(boolean symbol) {
		return symbol ? "£12£":messages.getString("MinesweeperTransfer_12");
	}

	/**
	   * Minesweeper [13]
	   */
	public static String Minesweeper(boolean symbol) {
		return symbol ? "£13£":messages.getString("Minesweeper_13");
	}

	/**
	   * Minesweepers [14]
	   */
	public static String Minesweepers(boolean symbol) {
		return symbol ? "£14£":messages.getString("Minesweepers_14");
	}

	/**
	   * Mission or transfer? [15]
	   */
	public static String MissionOrTransfer(boolean symbol) {
		return symbol ? "£15£":messages.getString("MissionOrTransfer_15");
	}

	/**
	   * Mission [16]
	   */
	public static String Mission(boolean symbol) {
		return symbol ? "£16£":messages.getString("Mission_16");
	}

	/**
	   * {0} $ [17]
	   */
	public static String MoneyFreight(boolean symbol, String arg0) {
		return symbol ? "£17§"+arg0+"£":MessageFormat.format(messages.getString("MoneyFreight_17"), arg0);
	}

	/**
	   * $ prod. in year {0} [18]
	   */
	public static String MoneyProductionInYear(boolean symbol, String arg0) {
		return symbol ? "£18§"+arg0+"£":MessageFormat.format(messages.getString("MoneyProductionInYear_18"), arg0);
	}

	/**
	   * $ production of the planets [19]
	   */
	public static String MoneyProductionOfPlanets(boolean symbol) {
		return symbol ? "£19£":messages.getString("MoneyProductionOfPlanets_19");
	}

	/**
	   * Messages [1A]
	   */
	public static String Messenger(boolean symbol) {
		return symbol ? "£1A£":messages.getString("Messenger_1A");
	}

	/**
	   * The loaded data require a newer<br>VEGA build. The minimum required build is <br>{0}, your VEGA build is<br>{1}.<br><br>Please download the newest build from <br>{2}. [1B]
	   */
	public static String MinBuild(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£1B§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("MinBuild_1B"), arg0, arg1, arg2);
	}

	/**
	   * Min: {0} (year {1}) [1C]
	   */
	public static String MinInYear(boolean symbol, String arg0, String arg1) {
		return symbol ? "£1C§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("MinInYear_1C"), arg0, arg1);
	}

	/**
	   * Mines (100) [1D]
	   */
	public static String Mine100Plural(boolean symbol) {
		return symbol ? "£1D£":messages.getString("Mine100Plural_1D");
	}

	/**
	   * M100 [1E]
	   */
	public static String Mine100Short(boolean symbol) {
		return symbol ? "£1E£":messages.getString("Mine100Short_1E");
	}

	/**
	   * Mine 100 [1F]
	   */
	public static String Mine100(boolean symbol) {
		return symbol ? "£1F£":messages.getString("Mine100_1F");
	}

	/**
	   * Mines (250) [1G]
	   */
	public static String Mine250Plural(boolean symbol) {
		return symbol ? "£1G£":messages.getString("Mine250Plural_1G");
	}

	/**
	   * M250 [1H]
	   */
	public static String Mine250Short(boolean symbol) {
		return symbol ? "£1H£":messages.getString("Mine250Short_1H");
	}

	/**
	   * Mine 250 [1I]
	   */
	public static String Mine250(boolean symbol) {
		return symbol ? "£1I£":messages.getString("Mine250_1I");
	}

	/**
	   * Mines (500) [1J]
	   */
	public static String Mine500Plural(boolean symbol) {
		return symbol ? "£1J£":messages.getString("Mine500Plural_1J");
	}

	/**
	   * M500 [1K]
	   */
	public static String Mine500Short(boolean symbol) {
		return symbol ? "£1K£":messages.getString("Mine500Short_1K");
	}

	/**
	   * Mine 500 [1L]
	   */
	public static String Mine500(boolean symbol) {
		return symbol ? "£1L£":messages.getString("Mine500_1L");
	}

	/**
	   * Mines (50) [1M]
	   */
	public static String Mine50Plural(boolean symbol) {
		return symbol ? "£1M£":messages.getString("Mine50Plural_1M");
	}

	/**
	   * M50 [1N]
	   */
	public static String Mine50Short(boolean symbol) {
		return symbol ? "£1N£":messages.getString("Mine50Short_1N");
	}

	/**
	   * Mine 50 [1O]
	   */
	public static String Mine50(boolean symbol) {
		return symbol ? "£1O£":messages.getString("Mine50_1O");
	}

	/**
	   * Mine field of strength {0} was swept. [1P]
	   */
	public static String MineFieldSwept(boolean symbol, String arg0) {
		return symbol ? "£1P§"+arg0+"£":MessageFormat.format(messages.getString("MineFieldSwept_1P"), arg0);
	}

	/**
	   * {0}: Mine was planted. [1Q]
	   */
	public static String MinePlanted(boolean symbol, String arg0) {
		return symbol ? "£1Q§"+arg0+"£":MessageFormat.format(messages.getString("MinePlanted_1Q"), arg0);
	}

	/**
	   * Mine [1R]
	   */
	public static String Mine(boolean symbol) {
		return symbol ? "£1R£":messages.getString("Mine_1R");
	}

	/**
	   * Minelayer (100) [1S]
	   */
	public static String Minelayer100(boolean symbol) {
		return symbol ? "£1S£":messages.getString("Minelayer100_1S");
	}

	/**
	   * Minelayer (250) [1T]
	   */
	public static String Minelayer250(boolean symbol) {
		return symbol ? "£1T£":messages.getString("Minelayer250_1T");
	}

	/**
	   * Minelayer (500) [1U]
	   */
	public static String Minelayer500(boolean symbol) {
		return symbol ? "£1U£":messages.getString("Minelayer500_1U");
	}

	/**
	   * Minelayer (50) [1V]
	   */
	public static String Minelayer50(boolean symbol) {
		return symbol ? "£1V£":messages.getString("Minelayer50_1V");
	}

	/**
	   * 1 minelayer arrived on planet {0}. [1W]
	   */
	public static String MinelayerArrived(boolean symbol, String arg0) {
		return symbol ? "£1W§"+arg0+"£":MessageFormat.format(messages.getString("MinelayerArrived_1W"), arg0);
	}

	/**
	   * 1 minelayer crashed on planet {0}. [1X]
	   */
	public static String MinelayerCrashed(boolean symbol, String arg0) {
		return symbol ? "£1X§"+arg0+"£":MessageFormat.format(messages.getString("MinelayerCrashed_1X"), arg0);
	}

	/**
	   * Minesweepers [1Y]
	   */
	public static String MinenraeumerPlural(boolean symbol) {
		return symbol ? "£1Y£":messages.getString("MinenraeumerPlural_1Y");
	}

	/**
	   * 1 minesweeper arrived on planet {0}. [1Z]
	   */
	public static String MinesweeperArrived(boolean symbol, String arg0) {
		return symbol ? "£1Z§"+arg0+"£":MessageFormat.format(messages.getString("MinesweeperArrived_1Z"), arg0);
	}

	/**
	   * $Pr [20]
	   */
	public static String MoneyProductionShort(boolean symbol) {
		return symbol ? "£20£":messages.getString("MoneyProductionShort_20");
	}

	/**
	   * $ production [21]
	   */
	public static String MoneyProduction(boolean symbol) {
		return symbol ? "£21£":messages.getString("MoneyProduction_21");
	}

	/**
	   * $Sup [22]
	   */
	public static String MoneySupplyShort(boolean symbol) {
		return symbol ? "£22£":messages.getString("MoneySupplyShort_22");
	}

	/**
	   * $ supply [23]
	   */
	public static String MoneySupply(boolean symbol) {
		return symbol ? "£23£":messages.getString("MoneySupply_23");
	}

	/**
	   * $ [24]
	   */
	public static String Money(boolean symbol) {
		return symbol ? "£24£":messages.getString("Money_24");
	}

	/**
	   * More... [25]
	   */
	public static String More(boolean symbol) {
		return symbol ? "£25£":messages.getString("More_25");
	}

	/**
	   * +++ Move entered +++ [26]
	   */
	public static String MoveEntered(boolean symbol) {
		return symbol ? "£26£":messages.getString("MoveEntered_26");
	}

	/**
	   * The moves do not belong to this year. [27]
	   */
	public static String MovesDoNotBelongToThisYear(boolean symbol) {
		return symbol ? "£27£":messages.getString("MovesDoNotBelongToThisYear_27");
	}

	/**
	   * Moves were not imported. [28]
	   */
	public static String MovesNotImported(boolean symbol) {
		return symbol ? "£28£":messages.getString("MovesNotImported_28");
	}

	/**
	   * The moves could not be transmitted to the server. [29]
	   */
	public static String MovesNotTransmittedToServer(boolean symbol) {
		return symbol ? "£29£":messages.getString("MovesNotTransmittedToServer_29");
	}

	/**
	   * The moves of the following e-mail players have not been imported yet: [2A]
	   */
	public static String MovesOfEmailPlayersNotYetImported(boolean symbol) {
		return symbol ? "£2A£":messages.getString("MovesOfEmailPlayersNotYetImported_2A");
	}

	/**
	   * Moves of {0} successfully imported. [2B]
	   */
	public static String MovesSuccessfullyImported(boolean symbol, String arg0) {
		return symbol ? "£2B§"+arg0+"£":MessageFormat.format(messages.getString("MovesSuccessfullyImported_2B"), arg0);
	}

	/**
	   * The moves were successfully transmitted to the server. [2C]
	   */
	public static String MovesTransmittedToServer(boolean symbol) {
		return symbol ? "£2C£":messages.getString("MovesTransmittedToServer_2C");
	}

	/**
	   * The initial contents could not be transmitted. [2D]
	   */
	public static String InitialScreenNotReceived(boolean symbol) {
		return symbol ? "£2D£":messages.getString("InitialScreenNotReceived_2D");
	}

	/**
	   * My name [2E]
	   */
	public static String MyName(boolean symbol) {
		return symbol ? "£2E£":messages.getString("MyName_2E");
	}

	/**
	   * Name of the game [2F]
	   */
	public static String NameOfGame(boolean symbol) {
		return symbol ? "£2F£":messages.getString("NameOfGame_2F");
	}

	/**
	   * Name [2G]
	   */
	public static String Name(boolean symbol) {
		return symbol ? "£2G£":messages.getString("Name_2G");
	}

	/**
	   * [Neutral fleet] [2H]
	   */
	public static String NeutralFleet(boolean symbol) {
		return symbol ? "£2H£":messages.getString("NeutralFleet_2H");
	}

	/**
	   * Neutral [2I]
	   */
	public static String Neutral(boolean symbol) {
		return symbol ? "£2I£":messages.getString("Neutral_2I");
	}

	/**
	   * New game board [2J]
	   */
	public static String NewGameBoard(boolean symbol) {
		return symbol ? "£2J£":messages.getString("NewGameBoard_2J");
	}

	/**
	   * New game [2K]
	   */
	public static String NewGamel(boolean symbol) {
		return symbol ? "£2K£":messages.getString("NewGamel_2K");
	}

	/**
	   * The new language settings become effective only after a restart of the application. The application will be closed now. [2L]
	   */
	public static String NewLanguageEffectiveAfterRestart(boolean symbol) {
		return symbol ? "£2L£":messages.getString("NewLanguageEffectiveAfterRestart_2L");
	}

	/**
	   * New local game [2M]
	   */
	public static String NewLocalGame(boolean symbol) {
		return symbol ? "£2M£":messages.getString("NewLocalGame_2M");
	}

	/**
	   * Hi {0},\n\nwelcome to VEGA! Your new user [{1}] on server {2}:{3} (build {4}) has been created and only needs to be activated.\n\nPlease proceed as follows:\n\n1. Select this whole e-mail text (for example, with ctrl + A), and copy it to the clipboard of your computer (for example, with ctrl + C).\n\n2. Start VEGA and select "Settings > VEGA server settings -> Credentials -> + -> Inactive user from the clipboard".\n\n3. Insert the contents of the clipboard into the text field and enter the password that you agreed on with your server administrator. Prss "OK".\n\n4. Activate the user with the button "Activate".\n\n5. Select your user from the drop-down list next to "Connect as player with the credentials" and set the checkbox.\n\n6. Close the dialog by pressing "OK".\n\nYour user is now active.\n\nEnjoy VEGA!\nYour server administrator [2N]
	   */
	public static String NewUserEmailBody(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4) {
		return symbol ? "£2N§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"£":MessageFormat.format(messages.getString("NewUserEmailBody_2N"), arg0, arg1, arg2, arg3, arg4);
	}

	/**
	   * To next event (animated) [2O]
	   */
	public static String NextEventAnimated(boolean symbol) {
		return symbol ? "£2O£":messages.getString("NextEventAnimated_2O");
	}

	/**
	   * To next event [2P]
	   */
	public static String NextEvent(boolean symbol) {
		return symbol ? "£2P£":messages.getString("NextEvent_2P");
	}

	/**
	   * Next [2Q]
	   */
	public static String Next(boolean symbol) {
		return symbol ? "£2Q£":messages.getString("Next_2Q");
	}

	/**
	   * There is no alliance on planet {0}. [2R]
	   */
	public static String NoAllianceOnPlanet(boolean symbol, String arg0) {
		return symbol ? "£2R§"+arg0+"£":MessageFormat.format(messages.getString("NoAllianceOnPlanet_2R"), arg0);
	}

	/**
	   * No alliance. [2S]
	   */
	public static String NoAlliance(boolean symbol) {
		return symbol ? "£2S£":messages.getString("NoAlliance_2S");
	}

	/**
	   * Connection error. Check the security code. [2T]
	   */
	public static String ConnectionErrorSecurityCode(boolean symbol) {
		return symbol ? "£2T£":messages.getString("ConnectionErrorSecurityCode_2T");
	}

	/**
	   * Credentials [2U]
	   */
	public static String Credentials(boolean symbol) {
		return symbol ? "£2U£":messages.getString("Credentials_2U");
	}

	/**
	   * There are no planets with alliances. [2V]
	   */
	public static String NoPlanetsWithAlliances(boolean symbol) {
		return symbol ? "£2V£":messages.getString("NoPlanetsWithAlliances_2V");
	}

	/**
	   * There are no spaceships. [2W]
	   */
	public static String NoSpaceships(boolean symbol) {
		return symbol ? "£2W£":messages.getString("NoSpaceships_2W");
	}

	/**
	   * No [2X]
	   */
	public static String No(boolean symbol) {
		return symbol ? "£2X£":messages.getString("No_2X");
	}

	/**
	   * Not connected [2Y]
	   */
	public static String NotConnected(boolean symbol) {
		return symbol ? "£2Y£":messages.getString("NotConnected_2Y");
	}

	/**
	   * You don't have enough battleships. [2Z]
	   */
	public static String NotEnoughBattleships(boolean symbol) {
		return symbol ? "£2Z£":messages.getString("NotEnoughBattleships_2Z");
	}

	/**
	   * OK [30]
	   */
	public static String OK(boolean symbol) {
		return symbol ? "£30£":messages.getString("OK_30");
	}

	/**
	   * off [31]
	   */
	public static String Off(boolean symbol) {
		return symbol ? "£31£":messages.getString("Off_31");
	}

	/**
	   * on [32]
	   */
	public static String On(boolean symbol) {
		return symbol ? "£32£":messages.getString("On_32");
	}

	/**
	   * The browser cannot be opened:\n{0} [33]
	   */
	public static String OpenBrowserError(boolean symbol, String arg0) {
		return symbol ? "£33§"+arg0+"£":MessageFormat.format(messages.getString("OpenBrowserError_33"), arg0);
	}

	/**
	   * Open output window [34]
	   */
	public static String OpenOutputWindow(boolean symbol) {
		return symbol ? "£34£":messages.getString("OpenOutputWindow_34");
	}

	/**
	   * Open PDF viewer? [35]
	   */
	public static String OpenPdfViewerQuestion(boolean symbol) {
		return symbol ? "£35£":messages.getString("OpenPdfViewerQuestion_35");
	}

	/**
	   * Other key [36]
	   */
	public static String OtherKey(boolean symbol) {
		return symbol ? "£36£":messages.getString("OtherKey_36");
	}

	/**
	   * Owner [37]
	   */
	public static String OwnerShort(boolean symbol) {
		return symbol ? "£37£":messages.getString("OwnerShort_37");
	}

	/**
	   * Page {0} [38]
	   */
	public static String Page(boolean symbol, String arg0) {
		return symbol ? "£38§"+arg0+"£":MessageFormat.format(messages.getString("Page_38"), arg0);
	}

	/**
	   * Password [39]
	   */
	public static String Password(boolean symbol) {
		return symbol ? "£39£":messages.getString("Password_39");
	}

	/**
	   * The passwords are not equal. [3A]
	   */
	public static String PasswordsNotEqual(boolean symbol) {
		return symbol ? "£3A£":messages.getString("PasswordsNotEqual_3A");
	}

	/**
	   * Paste from the clipboard in here [3B]
	   */
	public static String PasteClipboardHere(boolean symbol) {
		return symbol ? "£3B£":messages.getString("PasteClipboardHere_3B");
	}

	/**
	   * 1 patrol arrived on planet {0}. [3C]
	   */
	public static String PatrolArrived(boolean symbol, String arg0) {
		return symbol ? "£3C§"+arg0+"£":MessageFormat.format(messages.getString("PatrolArrived_3C"), arg0);
	}

	/**
	   * {0}: You captured {1} battleship(s) with destination {2} from {3}. [3D]
	   */
	public static String PatrolCapturedBattleships(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£3D§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("PatrolCapturedBattleships_3D"), arg0, arg1, arg2, arg3);
	}

	/**
	   * {0}: You captured a minelayer with destination {1} from {2}. [3E]
	   */
	public static String PatrolCapturedMinelayer(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£3E§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("PatrolCapturedMinelayer_3E"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a minesweeper with destination {1} from {2}. [3F]
	   */
	public static String PatrolCapturedMinesweeper(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£3F§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("PatrolCapturedMinesweeper_3F"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a patrol with destination {1} from {2}. [3G]
	   */
	public static String PatrolCapturedPatrol(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£3G§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("PatrolCapturedPatrol_3G"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a spy with destination {1} from {2}. [3H]
	   */
	public static String PatrolCapturedSpy(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£3H§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("PatrolCapturedSpy_3H"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a transporter with destination {1} from {2}. [3I]
	   */
	public static String PatrolCapturedTransporter(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£3I§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("PatrolCapturedTransporter_3I"), arg0, arg1, arg2);
	}

	/**
	   * 1 patrol crashed on planet {0}. [3J]
	   */
	public static String PatrolCrashed(boolean symbol, String arg0) {
		return symbol ? "£3J§"+arg0+"£":MessageFormat.format(messages.getString("PatrolCrashed_3J"), arg0);
	}

	/**
	   * Pat [3K]
	   */
	public static String PatrolShort(boolean symbol) {
		return symbol ? "£3K£":messages.getString("PatrolShort_3K");
	}

	/**
	   * Patrol (Transfer) [3L]
	   */
	public static String PatrolTransfer(boolean symbol) {
		return symbol ? "£3L£":messages.getString("PatrolTransfer_3L");
	}

	/**
	   * Patrol [3M]
	   */
	public static String Patrol(boolean symbol) {
		return symbol ? "£3M£":messages.getString("Patrol_3M");
	}

	/**
	   * Patrols [3N]
	   */
	public static String Patrols(boolean symbol) {
		return symbol ? "£3N£":messages.getString("Patrols_3N");
	}

	/**
	   * Patrols [3O]
	   */
	public static String PatrouillePlural(boolean symbol) {
		return symbol ? "£3O£":messages.getString("PatrouillePlural_3O");
	}

	/**
	   * Error: PDF viewer could not be opened. [3P]
	   */
	public static String PdfOpenError(boolean symbol) {
		return symbol ? "£3P£":messages.getString("PdfOpenError_3P");
	}

	/**
	   * PDF viewer was opened. [3Q]
	   */
	public static String PdfOpened(boolean symbol) {
		return symbol ? "£3Q£":messages.getString("PdfOpened_3Q");
	}

	/**
	   * Phys. Inventory [3R]
	   */
	public static String PhysicalInventoryShort(boolean symbol) {
		return symbol ? "£3R£":messages.getString("PhysicalInventoryShort_3R");
	}

	/**
	   * Physical Inventory [3S]
	   */
	public static String PhysicalInventory(boolean symbol) {
		return symbol ? "£3S£":messages.getString("PhysicalInventory_3S");
	}

	/**
	   * {0} has conquered the planet! [3T]
	   */
	public static String PlanetConquered(boolean symbol, String arg0) {
		return symbol ? "£3T§"+arg0+"£":MessageFormat.format(messages.getString("PlanetConquered_3T"), arg0);
	}

	/**
	   * Planet {0} is being attacked [3U]
	   */
	public static String PlanetIsAttacked(boolean symbol, String arg0) {
		return symbol ? "£3U§"+arg0+"£":MessageFormat.format(messages.getString("PlanetIsAttacked_3U"), arg0);
	}

	/**
	   * Pl [3V]
	   */
	public static String PlanetShort(boolean symbol) {
		return symbol ? "£3V£":messages.getString("PlanetShort_3V");
	}

	/**
	   * Planet [3W]
	   */
	public static String Planet(boolean symbol) {
		return symbol ? "£3W£":messages.getString("Planet_3W");
	}

	/**
	   * Planet {0} [3X]
	   */
	public static String Planet(boolean symbol, String arg0) {
		return symbol ? "£3X§"+arg0+"£":MessageFormat.format(messages.getString("Planet_3X"), arg0);
	}

	/**
	   * Planets, alliances, and spies [3Y]
	   */
	public static String PlanetsAlliancesSpies(boolean symbol) {
		return symbol ? "£3Y£":messages.getString("PlanetsAlliancesSpies_3Y");
	}

	/**
	   * Planets in year {0} [3Z]
	   */
	public static String PlanetsInYear(boolean symbol, String arg0) {
		return symbol ? "£3Z§"+arg0+"£":MessageFormat.format(messages.getString("PlanetsInYear_3Z"), arg0);
	}

	/**
	   * Planets produce battleships and $. [40]
	   */
	public static String PlanetsProducing(boolean symbol) {
		return symbol ? "£40£":messages.getString("PlanetsProducing_40");
	}

	/**
	   * Planets [41]
	   */
	public static String Planets(boolean symbol) {
		return symbol ? "£41£":messages.getString("Planets_41");
	}

	/**
	   * Player {0} capitulated. [42]
	   */
	public static String PlayerCapitulated(boolean symbol, String arg0) {
		return symbol ? "£42§"+arg0+"£":MessageFormat.format(messages.getString("PlayerCapitulated_42"), arg0);
	}

	/**
	   * >>> A player is entering his moves. Input is disabled. <<< [43]
	   */
	public static String PlayerEnteringMovesInputDisabled(boolean symbol) {
		return symbol ? "£43£":messages.getString("PlayerEnteringMovesInputDisabled_43");
	}

	/**
	   * Well, {0}. The game is over! [44]
	   */
	public static String PlayerGameOver(boolean symbol, String arg0) {
		return symbol ? "£44§"+arg0+"£":MessageFormat.format(messages.getString("PlayerGameOver_44"), arg0);
	}

	/**
	   * Player [45]
	   */
	public static String Player(boolean symbol) {
		return symbol ? "£45£":messages.getString("Player_45");
	}

	/**
	   * Players are waiting for me ({0}) [47]
	   */
	public static String PlayersAreWaiting(boolean symbol, String arg0) {
		return symbol ? "£47§"+arg0+"£":MessageFormat.format(messages.getString("PlayersAreWaiting_47"), arg0);
	}

	/**
	   * Players are waiting for your moves [48]
	   */
	public static String PlayersWaitingForInput(boolean symbol) {
		return symbol ? "£48£":messages.getString("PlayersWaitingForInput_48");
	}

	/**
	   * Players [49]
	   */
	public static String Players(boolean symbol) {
		return symbol ? "£49£":messages.getString("Players_49");
	}

	/**
	   * {0} points [4A]
	   */
	public static String Points2(boolean symbol, String arg0) {
		return symbol ? "£4A§"+arg0+"£":MessageFormat.format(messages.getString("Points2_4A"), arg0);
	}

	/**
	   * Points in year {0} [4B]
	   */
	public static String PointsInYear(boolean symbol, String arg0) {
		return symbol ? "£4B§"+arg0+"£":MessageFormat.format(messages.getString("PointsInYear_4B"), arg0);
	}

	/**
	   * Points [4C]
	   */
	public static String Points(boolean symbol) {
		return symbol ? "£4C£":messages.getString("Points_4C");
	}

	/**
	   * Production of battleships/year [4D]
	   */
	public static String ProductionOfBattleships(boolean symbol) {
		return symbol ? "£4D£":messages.getString("ProductionOfBattleships_4D");
	}

	/**
	   * Do you really want to publish the new game [{0}] on the server? [4E]
	   */
	public static String PublishGameQuestion(boolean symbol, String arg0) {
		return symbol ? "£4E§"+arg0+"£":MessageFormat.format(messages.getString("PublishGameQuestion_4E"), arg0);
	}

	/**
	   * Publish new game [4F]
	   */
	public static String PublishGame(boolean symbol) {
		return symbol ? "£4F£":messages.getString("PublishGame_4F");
	}

	/**
	   * Do you really want to quit VEGA Display? [4G]
	   */
	public static String QuitVegaDisplayQuestion(boolean symbol) {
		return symbol ? "£4G£":messages.getString("QuitVegaDisplayQuestion_4G");
	}

	/**
	   * Quit VEGA Display [4H]
	   */
	public static String QuitVegaDisplay(boolean symbol) {
		return symbol ? "£4H£":messages.getString("QuitVegaDisplay_4H");
	}

	/**
	   * Quit VEGA [4I]
	   */
	public static String QuitVega(boolean symbol) {
		return symbol ? "£4I£":messages.getString("QuitVega_4I");
	}

	/**
	   * Random [4J]
	   */
	public static String Random(boolean symbol) {
		return symbol ? "£4J£":messages.getString("Random_4J");
	}

	/**
	   * Read configuration... [4K]
	   */
	public static String ReadConfiguration(boolean symbol) {
		return symbol ? "£4K£":messages.getString("ReadConfiguration_4K");
	}

	/**
	   * Recently used e-mail addresses [4L]
	   */
	public static String RecentlyUsedEmailAddresses(boolean symbol) {
		return symbol ? "£4L£":messages.getString("RecentlyUsedEmailAddresses_4L");
	}

	/**
	   * Refresh [4N]
	   */
	public static String Refresh(boolean symbol) {
		return symbol ? "£4N£":messages.getString("Refresh_4N");
	}

	/**
	   * {1}/{0}/{2} {3}:{4} [4O]
	   */
	public static String ReleaseFormatted(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4) {
		return symbol ? "£4O§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"£":MessageFormat.format(messages.getString("ReleaseFormatted_4O"), arg0, arg1, arg2, arg3, arg4);
	}

	/**
	   * Server administration [4P]
	   */
	public static String ServerAdministration(boolean symbol) {
		return symbol ? "£4P£":messages.getString("ServerAdministration_4P");
	}

	/**
	   * Renew credentials [4Q]
	   */
	public static String RenewCredentials(boolean symbol) {
		return symbol ? "£4Q£":messages.getString("RenewCredentials_4Q");
	}

	/**
	   * Do you really want to update user [{0}]? BE CAREFUL: You will also renew the user credentials, so that the user needs to be activated again! [4R]
	   */
	public static String RenewUserCredentialsQuestion(boolean symbol, String arg0) {
		return symbol ? "£4R§"+arg0+"£":MessageFormat.format(messages.getString("RenewUserCredentialsQuestion_4R"), arg0);
	}

	/**
	   * (Repeat password) [4S]
	   */
	public static String RepeatPassword(boolean symbol) {
		return symbol ? "£4S£":messages.getString("RepeatPassword_4S");
	}

	/**
	   * Replay evaluation [4T]
	   */
	public static String ReplayEvaluation(boolean symbol) {
		return symbol ? "£4T£":messages.getString("ReplayEvaluation_4T");
	}

	/**
	   * Replay of the evaluation of year {0} [4U]
	   */
	public static String ReplayOfEvaluationOfYear(boolean symbol, String arg0) {
		return symbol ? "£4U§"+arg0+"£":MessageFormat.format(messages.getString("ReplayOfEvaluationOfYear_4U"), arg0);
	}

	/**
	   * Replay [4V]
	   */
	public static String Replay(boolean symbol) {
		return symbol ? "£4V£":messages.getString("Replay_4V");
	}

	/**
	   * Running since [4W]
	   */
	public static String RunningSince(boolean symbol) {
		return symbol ? "£4W£":messages.getString("RunningSince_4W");
	}

	/**
	   * File was saved successfully [4X]
	   */
	public static String SaveFileSuccess(boolean symbol) {
		return symbol ? "£4X£":messages.getString("SaveFileSuccess_4X");
	}

	/**
	   * Save game [4Y]
	   */
	public static String SaveGame(boolean symbol) {
		return symbol ? "£4Y£":messages.getString("SaveGame_4Y");
	}

	/**
	   * Save local game as [4Z]
	   */
	public static String SaveLocalGameAs(boolean symbol) {
		return symbol ? "£4Z£":messages.getString("SaveLocalGameAs_4Z");
	}

	/**
	   * Error when saving the log file:\n\n{0} [50]
	   */
	public static String SaveLogFileError(boolean symbol, String arg0) {
		return symbol ? "£50§"+arg0+"£":MessageFormat.format(messages.getString("SaveLogFileError_50"), arg0);
	}

	/**
	   * Save log file [51]
	   */
	public static String SaveLogFile(boolean symbol) {
		return symbol ? "£51£":messages.getString("SaveLogFile_51");
	}

	/**
	   * Unsaved changes [52]
	   */
	public static String UnsavedChanges(boolean symbol) {
		return symbol ? "£52£":messages.getString("UnsavedChanges_52");
	}

	/**
	   * Maximum number of connections reached. [53]
	   */
	public static String MaximumConnections(boolean symbol) {
		return symbol ? "£53£":messages.getString("MaximumConnections_53");
	}

	/**
	   * Security code [54]
	   */
	public static String SecurityCode(boolean symbol) {
		return symbol ? "£54£":messages.getString("SecurityCode_54");
	}

	/**
	   * Select [55]
	   */
	public static String Select(boolean symbol) {
		return symbol ? "£55£":messages.getString("Select_55");
	}

	/**
	   * Sell [56]
	   */
	public static String SellPrice(boolean symbol) {
		return symbol ? "£56£":messages.getString("SellPrice_56");
	}

	/**
	   * Sell [57]
	   */
	public static String Sell(boolean symbol) {
		return symbol ? "£57£":messages.getString("Sell_57");
	}

	/**
	   * The user [{0}] was successfully created on the server. How do you like to send the activation data to the user? [58]
	   */
	public static String SendActivationDataQuestion(boolean symbol, String arg0) {
		return symbol ? "£58§"+arg0+"£":MessageFormat.format(messages.getString("SendActivationDataQuestion_58"), arg0);
	}

	/**
	   * Please send the e-mail to the game host without changes. [59]
	   */
	public static String SendEmailToGameHost(boolean symbol) {
		return symbol ? "£59£":messages.getString("SendEmailToGameHost_59");
	}

	/**
	   * Please send the e-mail to the players without changes. [5A]
	   */
	public static String SendEmailToPlayers(boolean symbol) {
		return symbol ? "£5A£":messages.getString("SendEmailToPlayers_5A");
	}

	/**
	   * Send current game to all players [5B]
	   */
	public static String SendGameToAllPlayers(boolean symbol) {
		return symbol ? "£5B£":messages.getString("SendGameToAllPlayers_5B");
	}

	/**
	   * Server build [5C]
	   */
	public static String ServerBuild(boolean symbol) {
		return symbol ? "£5C£":messages.getString("ServerBuild_5C");
	}

	/**
	   * Do you want to take over the unsaved changes? [5D]
	   */
	public static String UnsavedChangesSave(boolean symbol) {
		return symbol ? "£5D£":messages.getString("UnsavedChangesSave_5D");
	}

	/**
	   * You haven't entered the server credentials yet [5E]
	   */
	public static String ServerCredentialsNotEntered(boolean symbol) {
		return symbol ? "£5E£":messages.getString("ServerCredentialsNotEntered_5E");
	}

	/**
	   * Server IP address [5F]
	   */
	public static String ServerIp(boolean symbol) {
		return symbol ? "£5F£":messages.getString("ServerIp_5F");
	}

	/**
	   * The server log does not contain any data. [5G]
	   */
	public static String ServerLogEmpty(boolean symbol) {
		return symbol ? "£5G£":messages.getString("ServerLogEmpty_5G");
	}

	/**
	   * Server port [5I]
	   */
	public static String ServerPort(boolean symbol) {
		return symbol ? "£5I£":messages.getString("ServerPort_5I");
	}

	/**
	   * Server setup aborted. Application terminated. [5J]
	   */
	public static String ServerSetupAborted(boolean symbol) {
		return symbol ? "£5J£":messages.getString("ServerSetupAborted_5J");
	}

	/**
	   * The VEGA server was shut down down successfully. [5K]
	   */
	public static String ServerShutdownSuccessfully(boolean symbol) {
		return symbol ? "£5K£":messages.getString("ServerShutdownSuccessfully_5K");
	}

	/**
	   * Server status [5L]
	   */
	public static String ServerStatus(boolean symbol) {
		return symbol ? "£5L£":messages.getString("ServerStatus_5L");
	}

	/**
	   * Server URL [5M]
	   */
	public static String ServerUrl(boolean symbol) {
		return symbol ? "£5M£":messages.getString("ServerUrl_5M");
	}

	/**
	   * Settings [5N]
	   */
	public static String Settings(boolean symbol) {
		return symbol ? "£5N£":messages.getString("Settings_5N");
	}

	/**
	   * Ships were launched. [5O]
	   */
	public static String ShipsLaunched(boolean symbol) {
		return symbol ? "£5O£":messages.getString("ShipsLaunched_5O");
	}

	/**
	   * Do you want to shut down the VEGA server? [5P]
	   */
	public static String ShutdownServerQuestion(boolean symbol) {
		return symbol ? "£5P£":messages.getString("ShutdownServerQuestion_5P");
	}

	/**
	   * Shut down server [5Q]
	   */
	public static String ShutdownServer(boolean symbol) {
		return symbol ? "£5Q£":messages.getString("ShutdownServer_5Q");
	}

	/**
	   * Spaceships [5R]
	   */
	public static String Spaceships(boolean symbol) {
		return symbol ? "£5R£":messages.getString("Spaceships_5R");
	}

	/**
	   * Spies [5S]
	   */
	public static String Spies(boolean symbol) {
		return symbol ? "£5S£":messages.getString("Spies_5S");
	}

	/**
	   * 1 spy arrived on planet {0}. [5T]
	   */
	public static String SpyArrived(boolean symbol, String arg0) {
		return symbol ? "£5T§"+arg0+"£":MessageFormat.format(messages.getString("SpyArrived_5T"), arg0);
	}

	/**
	   * 1 spy crashed on planet {0}. [5U]
	   */
	public static String SpyCrashed(boolean symbol, String arg0) {
		return symbol ? "£5U§"+arg0+"£":MessageFormat.format(messages.getString("SpyCrashed_5U"), arg0);
	}

	/**
	   * {0} dropped a spy on planet {1}. [5V]
	   */
	public static String SpyDropped(boolean symbol, String arg0, String arg1) {
		return symbol ? "£5V§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("SpyDropped_5V"), arg0, arg1);
	}

	/**
	   * Spy [5W]
	   */
	public static String SpyShort(boolean symbol) {
		return symbol ? "£5W£":messages.getString("SpyShort_5W");
	}

	/**
	   * Spy (Transfer) [5X]
	   */
	public static String SpyTransfer(boolean symbol) {
		return symbol ? "£5X£":messages.getString("SpyTransfer_5X");
	}

	/**
	   * Spy [5Y]
	   */
	public static String Spy(boolean symbol) {
		return symbol ? "£5Y£":messages.getString("Spy_5Y");
	}

	/**
	   * Start planet [5Z]
	   */
	public static String StartPlanet(boolean symbol) {
		return symbol ? "£5Z£":messages.getString("StartPlanet_5Z");
	}

	/**
	   * Start tutorial [60]
	   */
	public static String StartTutorial(boolean symbol) {
		return symbol ? "£60£":messages.getString("StartTutorial_60");
	}

	/**
	   * Start [61]
	   */
	public static String Start(boolean symbol) {
		return symbol ? "£61£":messages.getString("Start_61");
	}

	/**
	   * Start: {0} [62]
	   */
	public static String Start(boolean symbol, String arg0) {
		return symbol ? "£62§"+arg0+"£":MessageFormat.format(messages.getString("Start_62"), arg0);
	}

	/**
	   * Statistics [63]
	   */
	public static String Statistics(boolean symbol) {
		return symbol ? "£63£":messages.getString("Statistics_63");
	}

	/**
	   * Submit changes to server [64]
	   */
	public static String SubmitChangesToServer(boolean symbol) {
		return symbol ? "£64£":messages.getString("SubmitChangesToServer_64");
	}

	/**
	   * Success [65]
	   */
	public static String Success(boolean symbol) {
		return symbol ? "£65£":messages.getString("Success_65");
	}

	/**
	   * VEGA Display server [66]
	   */
	public static String Terminalserver(boolean symbol) {
		return symbol ? "£66£":messages.getString("Terminalserver_66");
	}

	/**
	   * Terminate alliance [67]
	   */
	public static String TerminateAlliance(boolean symbol) {
		return symbol ? "£67£":messages.getString("TerminateAlliance_67");
	}

	/**
	   * Text file [68]
	   */
	public static String TextFile(boolean symbol) {
		return symbol ? "£68£":messages.getString("TextFile_68");
	}

	/**
	   * There are no moves. Do you want to abort entering moves? [69]
	   */
	public static String ThereAreNoMoves(boolean symbol) {
		return symbol ? "£69£":messages.getString("ThereAreNoMoves_69");
	}

	/**
	   * This is the start planet. Enter another planet. [6A]
	   */
	public static String ThisIsTheStartPlanet(boolean symbol) {
		return symbol ? "£6A£":messages.getString("ThisIsTheStartPlanet_6A");
	}

	/**
	   * Timeout [s] [6B]
	   */
	public static String Timeout(boolean symbol) {
		return symbol ? "£6B£":messages.getString("Timeout_6B");
	}

	/**
	   * To [6C]
	   */
	public static String ToShort(boolean symbol) {
		return symbol ? "£6C£":messages.getString("ToShort_6C");
	}

	/**
	   * To which planet do you want to transfer {0} battleship(s)? [6D]
	   */
	public static String ToWhichPlanetBattleships(boolean symbol, String arg0) {
		return symbol ? "£6D§"+arg0+"£":MessageFormat.format(messages.getString("ToWhichPlanetBattleships_6D"), arg0);
	}

	/**
	   * To which planet do you want to transfer a minelayer (100)? [6E]
	   */
	public static String ToWhichPlanetMine100(boolean symbol) {
		return symbol ? "£6E£":messages.getString("ToWhichPlanetMine100_6E");
	}

	/**
	   * To which planet do you want to transfer a minelayer (250)? [6F]
	   */
	public static String ToWhichPlanetMine250(boolean symbol) {
		return symbol ? "£6F£":messages.getString("ToWhichPlanetMine250_6F");
	}

	/**
	   * To which planet do you want to transfer a minelayer (500)? [6G]
	   */
	public static String ToWhichPlanetMine500(boolean symbol) {
		return symbol ? "£6G£":messages.getString("ToWhichPlanetMine500_6G");
	}

	/**
	   * To which planet do you want to transfer a minelayer (50)? [6H]
	   */
	public static String ToWhichPlanetMine50(boolean symbol) {
		return symbol ? "£6H£":messages.getString("ToWhichPlanetMine50_6H");
	}

	/**
	   * To which planet do you want to transfer a minesweeper? [6I]
	   */
	public static String ToWhichPlanetMinesweeper(boolean symbol) {
		return symbol ? "£6I£":messages.getString("ToWhichPlanetMinesweeper_6I");
	}

	/**
	   * To which planet do you want to transfer a patrol? [6J]
	   */
	public static String ToWhichPlanetPatrol(boolean symbol) {
		return symbol ? "£6J£":messages.getString("ToWhichPlanetPatrol_6J");
	}

	/**
	   * To which planet do you want to transfer a spy? [6K]
	   */
	public static String ToWhichPlanetSpy(boolean symbol) {
		return symbol ? "£6K£":messages.getString("ToWhichPlanetSpy_6K");
	}

	/**
	   * To which planet do you want to transfer a transporter? [6L]
	   */
	public static String ToWhichPlanetTransporter(boolean symbol) {
		return symbol ? "£6L£":messages.getString("ToWhichPlanetTransporter_6L");
	}

	/**
	   * Transfer [6M]
	   */
	public static String Transfer(boolean symbol) {
		return symbol ? "£6M£":messages.getString("Transfer_6M");
	}

	/**
	   * 1 transporter arrived on planet {0}. [6N]
	   */
	public static String TransporterArrived(boolean symbol, String arg0) {
		return symbol ? "£6N§"+arg0+"£":MessageFormat.format(messages.getString("TransporterArrived_6N"), arg0);
	}

	/**
	   * 1 transporter crashed on planet {0}. [6O]
	   */
	public static String TransporterCrashed(boolean symbol, String arg0) {
		return symbol ? "£6O§"+arg0+"£":MessageFormat.format(messages.getString("TransporterCrashed_6O"), arg0);
	}

	/**
	   * Transporters [6P]
	   */
	public static String TransporterPlural(boolean symbol) {
		return symbol ? "£6P£":messages.getString("TransporterPlural_6P");
	}

	/**
	   * Tra [6Q]
	   */
	public static String TransporterShort(boolean symbol) {
		return symbol ? "£6Q£":messages.getString("TransporterShort_6Q");
	}

	/**
	   * Transporter [6R]
	   */
	public static String Transporter(boolean symbol) {
		return symbol ? "£6R£":messages.getString("Transporter_6R");
	}

	/**
	   * Transporters [6S]
	   */
	public static String Transporters(boolean symbol) {
		return symbol ? "£6S£":messages.getString("Transporters_6S");
	}

	/**
	   * Try again [6T]
	   */
	public static String TryAgain(boolean symbol) {
		return symbol ? "£6T£":messages.getString("TryAgain_6T");
	}

	/**
	   * >>> This action is not possible during the tutorial. [6U]
	   */
	public static String TutorialActionNotAllowed(boolean symbol) {
		return symbol ? "£6U£":messages.getString("TutorialActionNotAllowed_6U");
	}

	/**
	   * >>> Please carry out the task of the tutorial exactly. [6V]
	   */
	public static String TutorialActionNotExpected(boolean symbol) {
		return symbol ? "£6V£":messages.getString("TutorialActionNotExpected_6V");
	}

	/**
	   * Tutorial and demo games [6W]
	   */
	public static String TutorialAndDemoGames(boolean symbol) {
		return symbol ? "£6W£":messages.getString("TutorialAndDemoGames_6W");
	}

	/**
	   * Next >>> [6X]
	   */
	public static String TutorialButtonNext(boolean symbol) {
		return symbol ? "£6X£":messages.getString("TutorialButtonNext_6X");
	}

	/**
	   * Tutorial [6Y]
	   */
	public static String TutorialStartTitle(boolean symbol) {
		return symbol ? "£6Y£":messages.getString("TutorialStartTitle_6Y");
	}

	/**
	   * Welcome to VEGA! Are you here for the first time? Would you like to start the VEGA tutorial? [6Z]
	   */
	public static String TutorialStart(boolean symbol) {
		return symbol ? "£6Z£":messages.getString("TutorialStart_6Z");
	}

	/**
	   * ![size=1.5;color=1;bold|VEGA]\n\n![img=tutorial/iconShips.png]\n\nVEGA is a turn-based strategy game for 2 to 6 people in an 80s retro design. Whoever conquers the most planets with their spaceships wins the game. Play individually or in teams. Strategic thinking, diplomatic skills and a little luck are required. That's why VEGA is most fun in a friendly group of at least four players. There are no computer opponents, nor are there any graphic or sound effects.\n\nIn this tutorial, you will learn the most important rules of the game. You are Commander ![color=3|Alice] and start the conquest of space from your planet ![color=3|LM]. You have to assert yourself against your fellow players ![color=4|Bob] and ![color=5|Carol]. [70]
	   */
	public static String TutorialText00(boolean symbol) {
		return symbol ? "£70£":messages.getString("TutorialText00_70");
	}

	/**
	   * ![size=1.5;color=1;bold|The game board]\n\n![img=tutorial/board.png]\n\nThe VEGA game board consists of the square sectors AA to RT. The sectors have an edge length of one light year (1 ly). Planets are located at the center of some sectors. The planets ![color=3|LM], ![color=4|CH] and ![color=5|ND] belong to the players ![color=3|Alice], ![color=4|Bob] and ![color=5|Carol]. All other planets, e.g. ![color=2|JM], are neutral at the start of the game.\n\nSpaceships flying from one planet to another move in a straight line from the center of the starting sector to the center of the destination sector. This makes it very easy to calculate the distances, e.g. 2 ly from ![color=3|LM] to ![color=2|JM], 3 ly from ![color=3|LM] to ![color=2|LJ] and 3.606 ly from ![color=3|LM] to ![color=2|JJ]. [71]
	   */
	public static String TutorialText01(boolean symbol) {
		return symbol ? "£71£":messages.getString("TutorialText01_71");
	}

	/**
	   * ![size=1.5;color=1;bold|The year]\n\nVEGA is turn-based. A round is called a year. The game begins in year 1. The players enter their moves at the beginning of each year, then the computer evaluates the moves and moves the spaceships. Then the next year begins.\n\n![size=1.5;color=1;bold|Spaceships]\n\nIn VEGA there are the spaceship types: battleships, spies, transporters, patrols, minelayers and minesweepers. Each type flies at a certain constant speed, which allows the arrival time of a spaceship to be predicted exactly. Most spaceship types fly 2 light years per year. Battleships cover the distance from ![color=3|LM] to ![color=2|JM] in exactly one year. They need 1.5 light years to travel from ![color=3|LM] to ![color=2|LJ]. [72]
	   */
	public static String TutorialText02(boolean symbol) {
		return symbol ? "£72£":messages.getString("TutorialText02_72");
	}

	/**
	   * ![size=1.5;color=1;bold|Battleships]\n\nYou can conquer and defend planets with battleships. Battleships fly 2 light years per year. You can send battleships out individually or in fleets. Battleships can be recognized by the symbol ![img=tutorial/iconFighters12x12.png]. The battleships that are available on the planets for attacks and defense can be seen in the list at the top right of the screen. At the start of the game, there are 350 battleships on the players' planets, and 0 to 10 on the neutral planets.\n\nIn addition, a planet can have up to 900 defensive battleships that contribute to defense but cannot be used for attacks. Defensive battleships are not visible in the list. To see whether a foreign planet has defensive battleships, you must send a spy to the planet or form an alliance with the owner. The players' starting planets have 450 defensive battleships, the neutral planets have 0. [73]
	   */
	public static String TutorialText03(boolean symbol) {
		return symbol ? "£73£":messages.getString("TutorialText03_73");
	}

	/**
	   * ![size=1.5;color=1;bold|Planets]\n\nPlanets produce money ($) and spaceships. You also need to have the most planets to win the game.\n\nPlanets produce between $1 and $15 annually. A new battleship is created from each $ produced. You can reduce the production of battleships and buy other types of spaceships, defensive battleships and combat bonus points from the $ you have saved up. You can also use $ to increase the planet's production in $5 increments up to $100/year. [74]
	   */
	public static String TutorialText04(boolean symbol) {
		return symbol ? "£74£":messages.getString("TutorialText04_74");
	}

	/**
	   * ![size=1.5;color=1;bold|The planet editor]\n\nNow start entering your moves. Open the planet editor for your planet ![color=3|LM]. To do this, first press the ![inverse|1] key for "Alice" in the main menu. In the move entry menu, select ![inverse|0] for "Planet" and enter the name of your planet ![color=3|LM].\n\nYou see the data for your planet ![color=3|LM]. Like all of the players' planets, it has $30 in stock at the start of the game and produces $10, which means 10 new battleships per year. You also see the 450 defensive battleships that defend the planet in addition to the regular battleships. The planet has a combat strength of 100%.\n\nBelow you see a list of other types of spaceships that you can buy or sell for 2/3 of the purchase price. The prices for all items change annually, but are the same on all planets in the universe.\n\nUse the up and down arrow keys to navigate between the items, and use left and right to decrease or increase the number of an item.\n\n![color=1|Task]: ![italic;color=1|Move the cursor to the line "Production of battleships/year", reduce the value to 0 and accept the changes with the ENTER key.] [75]
	   */
	public static String TutorialText05(boolean symbol) {
		return symbol ? "£75£":messages.getString("TutorialText05_75");
	}

	/**
	   * ![size=1.5;color=1;bold|Launch battleships]\n\nSend battleships to the neighboring planets. Since your opponents are far away and there are no attacks from neutral planets, you don't have to worry about defending your planet ![color=3|LM] yet.\n\nIn the turn input menu ![inverse|1], select "Battleships". Enter the fleet's starting and destination planet and the number of battleships.\n\nThe number is determined by the number of battleships on the target planet, its annual battleship production and the flight time of your battleships. You will only see the battleship production if you have a spy on the planet or have formed an alliance with the owner. In the early stages of the game, it is still easy to conquer neutral planets. There are no defensive battleships on neutral planets, and their production is between 1 and 15 battleships per year.\n\nYou want to conquer planet ![color=2|JM]. There are 8 battleships there and you reach the planet in year 1. If you send out 35 battleships, you will take over the planet with 27 battleships.\n\n![color=1|Task]: ![italic;color=1|Send 35 battleships from your planet LM to planet JM.] [76]
	   */
	public static String TutorialText06(boolean symbol) {
		return symbol ? "£76£":messages.getString("TutorialText06_76");
	}

	/**
	   * ![size=1.5;color=1;bold|Launch more battleships (1)]\n\nPress the right arrow key. You will see the spaceships that are currently on the move at the top right, i.e. your 35 battleships on the way from ![color=3|LM] to ![color=2|JM].\n\nPress the right arrow key several times to see more information until you see the list of battleships again.\n\n![color=1|Task]: ![italic;color=1|Send 35 battleships from your planet LM to planet JJ.] [77]
	   */
	public static String TutorialText07(boolean symbol) {
		return symbol ? "£77£":messages.getString("TutorialText07_77");
	}

	/**
	   * ![size=1.5;color=1;bold|Buy a spy]\n\nSend a spy to another planet. The spy will install a radio transmitter there, which will give you full insight into the planet's data until the end of the game. You can  transfer a spy to a planet before deploying it from there. You can recognize spies by the symbol ![img=tutorial/iconScout12x12.png]. Spies in "mission" mode fly 4 light years per year, in "transfer" mode 2 light years per year.\n\n![color=1|Task]: ![italic;color=1|Open the planet editor of your planet LM and buy a spy. Confirm the purchase with the ENTER key.] [78]
	   */
	public static String TutorialText08(boolean symbol) {
		return symbol ? "£78£":messages.getString("TutorialText08_78");
	}

	/**
	   * ![size=1.5;color=1;bold|Launch a spy]\n\n![color=1|Task]: ![italic;color=1|Send the spy from planet LM to planet NI in mission mode.] [79]
	   */
	public static String TutorialText09(boolean symbol) {
		return symbol ? "£79£":messages.getString("TutorialText09_79");
	}

	/**
	   * ![size=1.5;color=1;bold|The annual evaluation]\n\nYou have now entered all the moves for year 1 for the tutorial. In a real game, you would launch further attacks on neighboring planets with your remaining 200 battleships, change your planet's production ![color=3|LM] or buy other types of spaceships.\n\nComplete the move entry with the ![inverse|TAB] key and confirm the query with ![inverse|1] "Yes". Start the evaluation with ![inverse|TAB] "Evaluation" and go through the evaluation with the ![inverse|TAB] or ![inverse|0] keys.\n\nThe computer implements the moves and moves the spaceships in 365 day increments. You can see on which day a spaceship reaches its destination in the spaceship list at the top right. If two events take place on the same day, the order is decided by chance. On the last day of the year, the first battleships reach the planets ![color=2|CF], ![color=2|CJ], ![color=2|LD], ![color=2|NF] and ![color=2|JM].\n\nAt the end of the evaluation, all planets produce $ and new battleships. After that, the new year 2 begins and you are back in the main menu. You see that a battleship fleet, a minelayer and two spies are still on the way, which will reach their goals in the following evaluations. [7A]
	   */
	public static String TutorialText10(boolean symbol) {
		return symbol ? "£7A£":messages.getString("TutorialText10_7A");
	}

	/**
	   * ![size=1.5;color=1;bold|The situation in year 2]\n\nStart the move entry for your user ![color=3|Alice] again. You have successfully conquered the planet ![color=3|JM]. Open the planet editor for the new planet ![color=3|JM]. The planet is very valuable with its annual production of $8. Therefore, you should protect it with defensive battleships, but the planet does not have enough $ for that.\n\nYou also see that ![color=4|Bob] has sent a spy to your new planet ![color=3|JM]. The spy reaches his goal in year 3.\n\nYour battleships to planet ![color=2|JJ] reach their goal in year 2. There are now 12 battleships on the planet, but this is not a problem for the 35 attacking battleships.\n\n![color=5|Carol], as if she had sensed your attack on planet ![color=2|LG], has sent a minelayer to sector ![color=2|LH] for a 100 mine. Learn more about mines in the next step. [7B]
	   */
	public static String TutorialText11(boolean symbol) {
		return symbol ? "£7B£":messages.getString("TutorialText11_7B");
	}

	/**
	   * ![size=1.5;color=1;bold|Buy a transporter]\n\nWith transporters you can transport up to $30 from planet to planet. If a transporter reaches a foreign planet, it is lost and its cargo is transferred to the planet. Transporters fly 2 light years per year and can be recognized by the symbol ![img=tutorial/iconTransport12x12.png].\n\nIn order to quickly buy a patrol that can intercept the approaching spy from ![color=4|Bob], you want to transport $ from the supply of the planet ![color=3|LM] to the planet ![color=3|JM].\n\n![color=1|Task]: ![italic;color=1|Open the planet editor of your planet LM and buy a single transporter. Then exit the planet editor with the ENTER key.] [7C]
	   */
	public static String TutorialText12(boolean symbol) {
		return symbol ? "£7C£":messages.getString("TutorialText12_7C");
	}

	/**
	   * ![size=1.5;color=1;bold|Launch a transporter]\n\n![color=1|Task]: ![italic;color=1|Send a transporter with $30 from planet LM to planet JM.] [7D]
	   */
	public static String TutorialText13(boolean symbol) {
		return symbol ? "£7D£":messages.getString("TutorialText13_7D");
	}

	/**
	   * ![size=1.5;color=1;bold|Evaluation of year 2]\n\nComplete the move input for year 2 and start the evaluation. During the evaluation, you will conquer the planet ![color=2|JJ] and have a spy on the planet ![color=2|NI]. From now on, you can view the data for the planet ![color=2|NI] at any time. In addition, your transporter will reach the planet ![color=3|JM] with $30.\n\nAfter the evaluation, start the move input for year 3. [7E]
	   */
	public static String TutorialText14(boolean symbol) {
		return symbol ? "£7E£":messages.getString("TutorialText14_7E");
	}

	/**
	   * ![size=1.5;color=1;bold|Buy a patrol]\n\nA patrol flies to a planet or any sector. It moves at 1 ly/year and captures enemy spies, transporters, minelayers, minesweepers, transferred patrols and battleship fleets with five ships or less within a radius of 1.5 ly. Captured ships do not move any further for the rest of the evaluation. You must select a new target planet for the captured ships in the following year.\n\nIf two enemy patrols meet in action, a fight ensues. The patrol that captures the other at a smaller angle to the direction of flight wins. If a patrol reaches a foreign planet, it is lost. If the target of the patrol is a sector, the patrol flies back to its starting planet and also observes on the way back. You can transfer patrols to another planet. They fly at 2 LY/year, but do not observe any ships.\n\nYou can recognize patrols by the symbol ![img=tutorial/iconPatrol12x12.png].\n\nThere are now enough $ on the planet ![color=3|JM] to buy a patrol.\n\n![color=1|Task]: ![italic;color=1|Open the planet editor of the planet JM and buy a patrol. Then exit the planet editor again with the ENTER key.] [7F]
	   */
	public static String TutorialText15(boolean symbol) {
		return symbol ? "£7F£":messages.getString("TutorialText15_7F");
	}

	/**
	   * ![size=1.5;color=1;bold|Launch a patrol]\n\nSend the patrol from the planet ![color=3|JM] towards the alien spy so that the spy gets into the patrol's observation circle and is captured.\n\n![color=1|Task]: ![italic;color=1|Send a patrol in "mission" mode from the planet JM to sector IL.] [7G]
	   */
	public static String TutorialText16(boolean symbol) {
		return symbol ? "£7G£":messages.getString("TutorialText16_7G");
	}

	/**
	   * ![size=1.5;color=1;bold|Evaluation of year 3]\n\nComplete the turn input for year 3 and start the evaluation.\n\nYour patrol detects and captures the foreign spy. The spy now waits for you to send him to another planet on the next turn input. He will fly there in "transfer" mode, i.e. he can be re-deployed from the target planet. Your patrol flies on to sector ![color=2|IL], which it will reach in year 4. There it will turn around and return to planet ![color=3|JM].\n\nMeanwhile, ![color=5|Carol] has placed a mine with a strength of 100 in the sector ![color=2|LH]. Shortly afterwards, your 80 battleships enter the mined sector and are destroyed. The mine then only has a strength of 20.\n\nAfter the evaluation, start the turn input for year 4. You will be asked for a new destination planet for the captured spy.\n\n![color=1|Task]: ![italic;color=1|Send the captured spy to planet JM.] [7H]
	   */
	public static String TutorialText18(boolean symbol) {
		return symbol ? "£7H£":messages.getString("TutorialText18_7H");
	}

	/**
	   * ![size=1.5;color=1;bold|Mines]\n\n![img=tutorial/mines.png]\n\nWith mines you can make sectors of the playing field impassable for battleships. Send a mine from a planet into a sector. There the minelayer turns into a live mine and destroys battleships that cross the sector boundaries, including your own battleships. You can recognize minelayers by the symbol ![img=tutorial/iconMineLayer12x12.png]. Minelayers fly 2 light years per year. You can transfer minelayers to one of your planets to use them from there.\n\nThere are 50, 100, 250 and 500 mines. A 50 mine can destroy up to 50 battleships before it disappears. Accordingly, a 100 mine can destroy 100 battleships, and so on. If you place more mines in a mined sector, the strengths of the mines add up to a large mine. You can recognize the strength of a mine by the number in the diamond symbol.\n\n![size=1.5;color=1;bold|Minesweeper]\n\nSend a minesweeper to a sector or planet. On the way there, it flies at 1 light year/year and removes the mines from all the sectors it flies through. If the minesweeper's destination is a sector, it turns around and flies back to its starting planet at 2 light years/year, but does not clear any mines on the way back.\n\nYou can recognize minesweepers by the symbol ![img=tutorial/iconMineSweeper12x12.png]. [7I]
	   */
	public static String TutorialText17(boolean symbol) {
		return symbol ? "£7I£":messages.getString("TutorialText17_7I");
	}

	/**
	   * ![size=1.5;color=1;bold|The black hole]\n\nIn the evaluation of year 8, the black hole appears for the first time in a random location. In each evaluation, it moves 2 light years in a random direction. All spaceships that are within a radius of 0.5 light years around the black hole are lost. If the black hole enters a sector with mines, all mines there are cleared. After a random number of years, the black hole disappears and after some time appears in a random location.\n\nThe black hole has the symbol ![img=tutorial/iconBlackHole12x12.png].\n\n![size=1.5;color=1;bold|Neutral battleship fleets]\n\nIn the evaluations of years 10, 15, 20, 25, etc., neutral battleship fleets start, flying from a random sector to a random planet. If a neutral fleet conquers a planet, the planet becomes neutral again. [7J]
	   */
	public static String TutorialText19(boolean symbol) {
		return symbol ? "£7J£":messages.getString("TutorialText19_7J");
	}

	/**
	   * ![size=1.5;color=1;bold|Alliances]\n\nPlayers can declare individual planets as alliance planets in order to jointly defend them and launch joint attacks from there. Losses from attacks and defenses are divided proportionally.\n\nAlliance partners can only control the battleships on the planet, not the other spaceship types. The alliance partners have full insight into the planet's production data and supplies, but cannot change them. Detailed information about alliances can be found in the game manual.\n\n![size=1.5;color=1;bold|Capitulate]\n\nIf the situation seems hopeless to you, you can use the "capitulate" command to raise the white flag and leave the game. All your planets become neutral, all your flying spaceships disintegrate, and you are eliminated from all alliances. However, the defensive battleships, the $ supplies, combat strengths, and spaceships that have not yet been launched remain on your former planets. The capitulation is implemented at the beginning of the evaluation.\n\n![size=1.5;color=1;bold|End of the game]\n\nThe game ends when the agreed playing time in years has passed or there is only one player left. You can end the game early with the consent of all players. The player with the most planets wins. [7K]
	   */
	public static String TutorialText20(boolean symbol) {
		return symbol ? "£7K£":messages.getString("TutorialText20_7K");
	}

	/**
	   * ![size=1.5;color=1;bold|End of the tutorial]\n\nCongratulations! You have reached the end of the tutorial and are now familiar with the most important rules of the game. Enjoy playing! [7L]
	   */
	public static String TutorialText21(boolean symbol) {
		return symbol ? "£7L£":messages.getString("TutorialText21_7L");
	}

	/**
	   * Tutorial - Step {0} of {1} [7M]
	   */
	public static String TutorialTitle(boolean symbol, String arg0, String arg1) {
		return symbol ? "£7M§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("TutorialTitle_7M"), arg0, arg1);
	}

	/**
	   * T [7N]
	   */
	public static String TypeShort(boolean symbol) {
		return symbol ? "£7N£":messages.getString("TypeShort_7N");
	}

	/**
	   * Type [7O]
	   */
	public static String Type(boolean symbol) {
		return symbol ? "£7O£":messages.getString("Type_7O");
	}

	/**
	   * Do you want to undo the last move? [7P]
	   */
	public static String UndoLastMoveQuestion(boolean symbol) {
		return symbol ? "£7P£":messages.getString("UndoLastMoveQuestion_7P");
	}

	/**
	   * Undo [7Q]
	   */
	public static String Undo(boolean symbol) {
		return symbol ? "£7Q£":messages.getString("Undo_7Q");
	}

	/**
	   * [Unknown] [7R]
	   */
	public static String Unknown(boolean symbol) {
		return symbol ? "£7R£":messages.getString("Unknown_7R");
	}

	/**
	   * Do you really want to update user [{0}]? [7S]
	   */
	public static String UpdateUserQuestion(boolean symbol, String arg0) {
		return symbol ? "£7S§"+arg0+"£":MessageFormat.format(messages.getString("UpdateUserQuestion_7S"), arg0);
	}

	/**
	   * The user was activated successfully. [7U]
	   */
	public static String UserActivationSuccess(boolean symbol) {
		return symbol ? "£7U£":messages.getString("UserActivationSuccess_7U");
	}

	/**
	   * The user [{0}] was successfully deleted from the server. [7V]
	   */
	public static String UserDeleted(boolean symbol, String arg0) {
		return symbol ? "£7V§"+arg0+"£":MessageFormat.format(messages.getString("UserDeleted_7V"), arg0);
	}

	/**
	   * User ID [7W]
	   */
	public static String UserId(boolean symbol) {
		return symbol ? "£7W£":messages.getString("UserId_7W");
	}

	/**
	   * The user name [{0}] is invalid.\nThe length of a user name must be between {1} and {2} characters.\nIt must only contain the characters a-z, A-Z, and 0-9. [7X]
	   */
	public static String UserNameInvalid(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£7X§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("UserNameInvalid_7X"), arg0, arg1, arg2);
	}

	/**
	   * The user ID [{0}] does not exist on the server. [7Y]
	   */
	public static String UserNotExists(boolean symbol, String arg0) {
		return symbol ? "£7Y§"+arg0+"£":MessageFormat.format(messages.getString("UserNotExists_7Y"), arg0);
	}

	/**
	   * The user [{0}] does not participate in this game. [7Z]
	   */
	public static String UserNotParticipating(boolean symbol, String arg0) {
		return symbol ? "£7Z§"+arg0+"£":MessageFormat.format(messages.getString("UserNotParticipating_7Z"), arg0);
	}

	/**
	   * The user [{0}] was updated successfully on the server. [80]
	   */
	public static String UserUpdated(boolean symbol, String arg0) {
		return symbol ? "£80§"+arg0+"£":MessageFormat.format(messages.getString("UserUpdated_80"), arg0);
	}

	/**
	   * Users [81]
	   */
	public static String Users(boolean symbol) {
		return symbol ? "£81£":messages.getString("Users_81");
	}

	/**
	   * VEGA Display server [84]
	   */
	public static String VegaDisplayServer(boolean symbol) {
		return symbol ? "£84£":messages.getString("VegaDisplayServer_84");
	}

	/**
	   * VEGA Display [85]
	   */
	public static String VegaDisplay(boolean symbol) {
		return symbol ? "£85£":messages.getString("VegaDisplay_85");
	}

	/**
	   * VEGA Displays passive while moves are being entered [86]
	   */
	public static String VegaDisplaysPassive(boolean symbol) {
		return symbol ? "£86£":messages.getString("VegaDisplaysPassive_86");
	}

	/**
	   * VEGA language [87]
	   */
	public static String VegaLanguage(boolean symbol) {
		return symbol ? "£87£":messages.getString("VegaLanguage_87");
	}

	/**
	   * Connect as player with the credentials [88]
	   */
	public static String ConnectAsPlayerWithCredentials(boolean symbol) {
		return symbol ? "£88£":messages.getString("ConnectAsPlayerWithCredentials_88");
	}

	/**
	   * Welcome to the setup of the VEGA server! [89]
	   */
	public static String VegaServerSetupWelcome(boolean symbol) {
		return symbol ? "£89£":messages.getString("VegaServerSetupWelcome_89");
	}

	/**
	   * [VEGA] server user {0} [8A]
	   */
	public static String VegaServer(boolean symbol, String arg0) {
		return symbol ? "£8A§"+arg0+"£":MessageFormat.format(messages.getString("VegaServer_8A"), arg0);
	}

	/**
	   * VEGA [8B]
	   */
	public static String Vega(boolean symbol) {
		return symbol ? "£8B£":messages.getString("Vega_8B");
	}

	/**
	   * Please wait for the next e-mail from the game host. [8C]
	   */
	public static String WaitForEmailFromGameHost(boolean symbol) {
		return symbol ? "£8C£":messages.getString("WaitForEmailFromGameHost_8C");
	}

	/**
	   * Please wait for the evaluation. [8D]
	   */
	public static String WaitForEvaluation(boolean symbol) {
		return symbol ? "£8D£":messages.getString("WaitForEvaluation_8D");
	}

	/**
	   * I am waiting for other players ({0}) [8E]
	   */
	public static String WaitingForOtherPlayers(boolean symbol, String arg0) {
		return symbol ? "£8E§"+arg0+"£":MessageFormat.format(messages.getString("WaitingForOtherPlayers_8E"), arg0);
	}

	/**
	   * Web server configuration [8F]
	   */
	public static String WebServerConfiguration(boolean symbol) {
		return symbol ? "£8F£":messages.getString("WebServerConfiguration_8F");
	}

	/**
	   * Web server not activated [8G]
	   */
	public static String WebServerInactive(boolean symbol) {
		return symbol ? "£8G£":messages.getString("WebServerInactive_8G");
	}

	/**
	   * Inventory {0} [8H]
	   */
	public static String WebServerInventory(boolean symbol, String arg0) {
		return symbol ? "£8H§"+arg0+"£":MessageFormat.format(messages.getString("WebServerInventory_8H"), arg0);
	}

	/**
	   * Resource {0} not found on VEGA Web server. [8I]
	   */
	public static String WebServerResourceNotFound(boolean symbol, String arg0) {
		return symbol ? "£8I§"+arg0+"£":MessageFormat.format(messages.getString("WebServerResourceNotFound_8I"), arg0);
	}

	/**
	   * Web server [8J]
	   */
	public static String WebServer(boolean symbol) {
		return symbol ? "£8J£":messages.getString("WebServer_8J");
	}

	/**
	   * Which type? [8K]
	   */
	public static String WhichType(boolean symbol) {
		return symbol ? "£8K£":messages.getString("WhichType_8K");
	}

	/**
	   * Who is missing? [8L]
	   */
	public static String WhoIsMissing(boolean symbol) {
		return symbol ? "£8L£":messages.getString("WhoIsMissing_8L");
	}

	/**
	   * Write e-mail [8M]
	   */
	public static String WriteEmail(boolean symbol) {
		return symbol ? "£8M£":messages.getString("WriteEmail_8M");
	}

	/**
	   * The year has already been evaluated. [8N]
	   */
	public static String YearAlreadyEvaluated(boolean symbol) {
		return symbol ? "£8N£":messages.getString("YearAlreadyEvaluated_8N");
	}

	/**
	   * Year -\n [8O]
	   */
	public static String YearBack(boolean symbol) {
		return symbol ? "£8O£":messages.getString("YearBack_8O");
	}

	/**
	   * Y{0}-D{1} [8P]
	   */
	public static String YearDayShort(boolean symbol, String arg0, String arg1) {
		return symbol ? "£8P§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("YearDayShort_8P"), arg0, arg1);
	}

	/**
	   * Year {0}, day {1} [8Q]
	   */
	public static String YearDay(boolean symbol, String arg0, String arg1) {
		return symbol ? "£8Q§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("YearDay_8Q"), arg0, arg1);
	}

	/**
	   * Year {0}, end of the year [8R]
	   */
	public static String YearEndOfYear(boolean symbol, String arg0) {
		return symbol ? "£8R§"+arg0+"£":MessageFormat.format(messages.getString("YearEndOfYear_8R"), arg0);
	}

	/**
	   * Y{0}-End [8S]
	   */
	public static String YearEndShort(boolean symbol, String arg0) {
		return symbol ? "£8S§"+arg0+"£":MessageFormat.format(messages.getString("YearEndShort_8S"), arg0);
	}

	/**
	   * Year +\n [8T]
	   */
	public static String YearForward(boolean symbol) {
		return symbol ? "£8T£":messages.getString("YearForward_8T");
	}

	/**
	   * Year {0} of {1} [8U]
	   */
	public static String YearOf(boolean symbol, String arg0, String arg1) {
		return symbol ? "£8U§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("YearOf_8U"), arg0, arg1);
	}

	/**
	   * Year {0} of [8V]
	   */
	public static String YearOf(boolean symbol, String arg0) {
		return symbol ? "£8V§"+arg0+"£":MessageFormat.format(messages.getString("YearOf_8V"), arg0);
	}

	/**
	   * Year {0} [8W]
	   */
	public static String Year(boolean symbol, String arg0) {
		return symbol ? "£8W§"+arg0+"£":MessageFormat.format(messages.getString("Year_8W"), arg0);
	}

	/**
	   * Years [8X]
	   */
	public static String Years(boolean symbol) {
		return symbol ? "£8X£":messages.getString("Years_8X");
	}

	/**
	   * Yes [8Y]
	   */
	public static String Yes(boolean symbol) {
		return symbol ? "£8Y£":messages.getString("Yes_8Y");
	}

	/**
	   * You are not the host of game {0}. [8Z]
	   */
	public static String YouAreNotGameHost(boolean symbol, String arg0) {
		return symbol ? "£8Z§"+arg0+"£":MessageFormat.format(messages.getString("YouAreNotGameHost_8Z"), arg0);
	}

	/**
	   * You are not the owner of the planet. [90]
	   */
	public static String YouAreNotOwnerOfPlanet(boolean symbol) {
		return symbol ? "£90£":messages.getString("YouAreNotOwnerOfPlanet_90");
	}

	/**
	   * You cannot start more than {0} battleship(s). [91]
	   */
	public static String YouCannotStartMoreBattleships(boolean symbol, String arg0) {
		return symbol ? "£91§"+arg0+"£":MessageFormat.format(messages.getString("YouCannotStartMoreBattleships_91"), arg0);
	}

	/**
	   * You cannot transport this amount of $. [92]
	   */
	public static String YouCannotTransportThisAmountOfMoney(boolean symbol) {
		return symbol ? "£92£":messages.getString("YouCannotTransportThisAmountOfMoney_92");
	}

	/**
	   * You have no planets. [93]
	   */
	public static String YouHaveNoPlanets(boolean symbol) {
		return symbol ? "£93£":messages.getString("YouHaveNoPlanets_93");
	}

	/**
	   * Abort replay [94]
	   */
	public static String AbortReplay(boolean symbol) {
		return symbol ? "£94£":messages.getString("AbortReplay_94");
	}

	/**
	   * About VEGA [95]
	   */
	public static String AboutVega(boolean symbol) {
		return symbol ? "£95£":messages.getString("AboutVega_95");
	}

	/**
	   * Accept changes [96]
	   */
	public static String AcceptChanges(boolean symbol) {
		return symbol ? "£96£":messages.getString("AcceptChanges_96");
	}

	/**
	   * --- Action was cancelled --- [97]
	   */
	public static String ActionCancelled(boolean symbol) {
		return symbol ? "£97£":messages.getString("ActionCancelled_97");
	}

	/**
	   * Action not possible. [98]
	   */
	public static String ActionNotPossible(boolean symbol) {
		return symbol ? "£98£":messages.getString("ActionNotPossible_98");
	}

	/**
	   * Action not possible: {0} [99]
	   */
	public static String ActionNotPossible(boolean symbol, String arg0) {
		return symbol ? "£99§"+arg0+"£":MessageFormat.format(messages.getString("ActionNotPossible_99"), arg0);
	}

	/**
	   * Activate server [9A]
	   */
	public static String ActivateServer(boolean symbol) {
		return symbol ? "£9A£":messages.getString("ActivateServer_9A");
	}

	/**
	   * Activate user [9B]
	   */
	public static String ActivateUser(boolean symbol) {
		return symbol ? "£9B£":messages.getString("ActivateUser_9B");
	}

	/**
	   * Activate [9C]
	   */
	public static String Activate(boolean symbol) {
		return symbol ? "£9C£":messages.getString("Activate_9C");
	}

	/**
	   * The activation password must be at least three characters long. [9D]
	   */
	public static String ActivationPasswordTooShort(boolean symbol) {
		return symbol ? "£9D£":messages.getString("ActivationPasswordTooShort_9D");
	}

	/**
	   * Activation password [9E]
	   */
	public static String ActivationPassword(boolean symbol) {
		return symbol ? "£9E£":messages.getString("ActivationPassword_9E");
	}

	/**
	   * Do you want to add the positions to the leaderboard? [9F]
	   */
	public static String AddToHighScoreListQuestion(boolean symbol) {
		return symbol ? "£9F£":messages.getString("AddToHighScoreListQuestion_9F");
	}

	/**
	   * Address separator [9G]
	   */
	public static String AddressSeparator(boolean symbol) {
		return symbol ? "£9G£":messages.getString("AddressSeparator_9G");
	}

	/**
	   * Administrator credentials [9H]
	   */
	public static String AdminCredentials(boolean symbol) {
		return symbol ? "£9H£":messages.getString("AdminCredentials_9H");
	}

	/**
	   * VEGA server settings [9I]
	   */
	public static String ServerSettings(boolean symbol) {
		return symbol ? "£9I£":messages.getString("ServerSettings_9I");
	}

	/**
	   * Do you agree with this game board? [9J]
	   */
	public static String AgreeWithGameBoardQuestion(boolean symbol) {
		return symbol ? "£9J£":messages.getString("AgreeWithGameBoardQuestion_9J");
	}

	/**
	   * All battleships [9K]
	   */
	public static String AllBattleships(boolean symbol) {
		return symbol ? "£9K£":messages.getString("AllBattleships_9K");
	}

	/**
	   * The moves of all e-mail players have been imported. [9L]
	   */
	public static String AllEmailPlayerMovesImported(boolean symbol) {
		return symbol ? "£9L£":messages.getString("AllEmailPlayerMovesImported_9L");
	}

	/**
	   * All off [9M]
	   */
	public static String AllOff(boolean symbol) {
		return symbol ? "£9M£":messages.getString("AllOff_9M");
	}

	/**
	   * All on [9N]
	   */
	public static String AllOn(boolean symbol) {
		return symbol ? "£9N£":messages.getString("AllOn_9N");
	}

	/**
	   * You must not combine '0' with other inputs. [9O]
	   */
	public static String AllianceDefinitionError(boolean symbol) {
		return symbol ? "£9O£":messages.getString("AllianceDefinitionError_9O");
	}

	/**
	   * You must also enter the current alliance members! [9P]
	   */
	public static String AllianceOwnerNotIncluded2(boolean symbol) {
		return symbol ? "£9P£":messages.getString("AllianceOwnerNotIncluded2_9P");
	}

	/**
	   * You must at least enter the owner of the planet and yourself! [9Q]
	   */
	public static String AllianceOwnerNotIncluded(boolean symbol) {
		return symbol ? "£9Q£":messages.getString("AllianceOwnerNotIncluded_9Q");
	}

	/**
	   * Alliance [9R]
	   */
	public static String AllianceShort(boolean symbol) {
		return symbol ? "£9R£":messages.getString("AllianceShort_9R");
	}

	/**
	   * Alliance structure on planet {0} [9S]
	   */
	public static String AllianceStructureOnPlanet(boolean symbol, String arg0) {
		return symbol ? "£9S§"+arg0+"£":MessageFormat.format(messages.getString("AllianceStructureOnPlanet_9S"), arg0);
	}

	/**
	   * Alliance [9T]
	   */
	public static String Alliance(boolean symbol) {
		return symbol ? "£9T£":messages.getString("Alliance_9T");
	}

	/**
	   * Alliances [9U]
	   */
	public static String Alliances(boolean symbol) {
		return symbol ? "£9U£":messages.getString("Alliances_9U");
	}

	/**
	   * Allied b'ships [9V]
	   */
	public static String AlliedBattleships(boolean symbol) {
		return symbol ? "£9V£":messages.getString("AlliedBattleships_9V");
	}

	/**
	   * Allied:{0} [9W]
	   */
	public static String Allied(boolean symbol, String arg0) {
		return symbol ? "£9W§"+arg0+"£":MessageFormat.format(messages.getString("Allied_9W"), arg0);
	}

	/**
	   * Are you really sure? [9X]
	   */
	public static String AreYouSure(boolean symbol) {
		return symbol ? "£9X£":messages.getString("AreYouSure_9X");
	}

	/**
	   * Arrival: [9Y]
	   */
	public static String Arrival2(boolean symbol) {
		return symbol ? "£9Y£":messages.getString("Arrival2_9Y");
	}

	/**
	   * Arrival [9Z]
	   */
	public static String Arrival(boolean symbol) {
		return symbol ? "£9Z£":messages.getString("Arrival_9Z");
	}

	/**
	   * You have to assign users to all players. [A0]
	   */
	public static String AssignUsersToAllPlayers(boolean symbol) {
		return symbol ? "£A0£":messages.getString("AssignUsersToAllPlayers_A0");
	}

	/**
	   * Attack failed. [A1]
	   */
	public static String AttackFailed(boolean symbol) {
		return symbol ? "£A1£":messages.getString("AttackFailed_A1");
	}

	/**
	   * Attributes [A3]
	   */
	public static String Attributes(boolean symbol) {
		return symbol ? "£A3£":messages.getString("Attributes_A3");
	}

	/**
	   * Authentication file [A4]
	   */
	public static String AuthenticationFile(boolean symbol) {
		return symbol ? "£A4£":messages.getString("AuthenticationFile_A4");
	}

	/**
	   * Automatic save [A5]
	   */
	public static String AutomaticSave(boolean symbol) {
		return symbol ? "£A5£":messages.getString("AutomaticSave_A5");
	}

	/**
	   * Back [A6]
	   */
	public static String Back(boolean symbol) {
		return symbol ? "£A6£":messages.getString("Back_A6");
	}

	/**
	   * BsP [A7]
	   */
	public static String BattleshipProductionShort(boolean symbol) {
		return symbol ? "£A7£":messages.getString("BattleshipProductionShort_A7");
	}

	/**
	   * Battleship production [A8]
	   */
	public static String BattleshipProduction(boolean symbol) {
		return symbol ? "£A8£":messages.getString("BattleshipProduction_A8");
	}

	/**
	   * {0} battleship(s) arrived on planet {1}. [A9]
	   */
	public static String BattleshipsArrivedAtPlanet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£A9§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("BattleshipsArrivedAtPlanet_A9"), arg0, arg1);
	}

	/**
	   * Battleships in year {0} [AA]
	   */
	public static String BattleshipsInYear(boolean symbol, String arg0) {
		return symbol ? "£AA§"+arg0+"£":MessageFormat.format(messages.getString("BattleshipsInYear_AA"), arg0);
	}

	/**
	   * {0}: {1} battleship(s) killed by a mine. The mine was destroyed. [AB]
	   */
	public static String BattleshipsKilledByMine2(boolean symbol, String arg0, String arg1) {
		return symbol ? "£AB§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("BattleshipsKilledByMine2_AB"), arg0, arg1);
	}

	/**
	   * {0}: {1} battleship(s) destroyed by a mine. [AC]
	   */
	public static String BattleshipsKilledByMine(boolean symbol, String arg0, String arg1) {
		return symbol ? "£AC§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("BattleshipsKilledByMine_AC"), arg0, arg1);
	}

	/**
	   * {1}: {0} battleship(s) have to leave planet {2}, because the alliance was terminated. [AD]
	   */
	public static String BattleshipsMustLeavePlanet(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£AD§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("BattleshipsMustLeavePlanet_AD"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Battleships cannot be launched from planet {1}. [AE]
	   */
	public static String BattleshipsNotLaunchedFromPlanet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£AE§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("BattleshipsNotLaunchedFromPlanet_AE"), arg0, arg1);
	}

	/**
	   * Btls [AF]
	   */
	public static String BattleshipsShort(boolean symbol) {
		return symbol ? "£AF£":messages.getString("BattleshipsShort_AF");
	}

	/**
	   * The battleships are waiting outside the planet for a new destination. [AG]
	   */
	public static String BattleshipsWaiting(boolean symbol) {
		return symbol ? "£AG£":messages.getString("BattleshipsWaiting_AG");
	}

	/**
	   * Battleships [AH]
	   */
	public static String Battleships(boolean symbol) {
		return symbol ? "£AH£":messages.getString("Battleships_AH");
	}

	/**
	   * Beginning of the year [AI]
	   */
	public static String BeginningOfYear(boolean symbol) {
		return symbol ? "£AI£":messages.getString("BeginningOfYear_AI");
	}

	/**
	   * The Black Hole destroyed {0} battleships. [AJ]
	   */
	public static String BlackHoleDestroyedBattleships(boolean symbol, String arg0) {
		return symbol ? "£AJ§"+arg0+"£":MessageFormat.format(messages.getString("BlackHoleDestroyedBattleships_AJ"), arg0);
	}

	/**
	   * The Black Hole destroyed 1 minelayer. [AK]
	   */
	public static String BlackHoleMine(boolean symbol) {
		return symbol ? "£AK£":messages.getString("BlackHoleMine_AK");
	}

	/**
	   * The Black Hole swept a mine field of strength {0}. [AL]
	   */
	public static String BlackHoleMines(boolean symbol, String arg0) {
		return symbol ? "£AL§"+arg0+"£":MessageFormat.format(messages.getString("BlackHoleMines_AL"), arg0);
	}

	/**
	   * The Black Hole destroyed 1 minesweeper. [AM]
	   */
	public static String BlackHoleMinesweeper(boolean symbol) {
		return symbol ? "£AM£":messages.getString("BlackHoleMinesweeper_AM");
	}

	/**
	   * The Black Hole destroyed 1 patrol. [AN]
	   */
	public static String BlackHolePatrol(boolean symbol) {
		return symbol ? "£AN£":messages.getString("BlackHolePatrol_AN");
	}

	/**
	   * The Black Hole destroyed 1 spy. [AO]
	   */
	public static String BlackHoleSpy(boolean symbol) {
		return symbol ? "£AO£":messages.getString("BlackHoleSpy_AO");
	}

	/**
	   * The Black Hole destroyed 1 transporter. [AP]
	   */
	public static String BlackHoleTransport(boolean symbol) {
		return symbol ? "£AP£":messages.getString("BlackHoleTransport_AP");
	}

	/**
	   * Board [AQ]
	   */
	public static String Board(boolean symbol) {
		return symbol ? "£AQ£":messages.getString("Board_AQ");
	}

	/**
	   * Buy [AR]
	   */
	public static String BuyPrice(boolean symbol) {
		return symbol ? "£AR£":messages.getString("BuyPrice_AR");
	}

	/**
	   * Defensive battleships (+{0}/-{1}) [AS]
	   */
	public static String BuySellDefensiveBattleships(boolean symbol, String arg0, String arg1) {
		return symbol ? "£AS§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("BuySellDefensiveBattleships_AS"), arg0, arg1);
	}

	/**
	   * Buy\n [AT]
	   */
	public static String Buy(boolean symbol) {
		return symbol ? "£AT£":messages.getString("Buy_AT");
	}

	/**
	   * Cancel [AU]
	   */
	public static String Cancel(boolean symbol) {
		return symbol ? "£AU£":messages.getString("Cancel_AU");
	}

	/**
	   * Capitulate [AV]
	   */
	public static String Capitulate(boolean symbol) {
		return symbol ? "£AV£":messages.getString("Capitulate_AV");
	}

	/**
	   * Do you really want to change the log level of the server to "{0}"? [AW]
	   */
	public static String ChangeLogLevelQuestion(boolean symbol, String arg0) {
		return symbol ? "£AW§"+arg0+"£":MessageFormat.format(messages.getString("ChangeLogLevelQuestion_AW"), arg0);
	}

	/**
	   * Change log level [AX]
	   */
	public static String ChangeLogLevel(boolean symbol) {
		return symbol ? "£AX£":messages.getString("ChangeLogLevel_AX");
	}

	/**
	   * Change selection [AY]
	   */
	public static String ChangeSelection(boolean symbol) {
		return symbol ? "£AY£":messages.getString("ChangeSelection_AY");
	}

	/**
	   * VEGA build [AZ]
	   */
	public static String ClientBuild(boolean symbol) {
		return symbol ? "£AZ£":messages.getString("ClientBuild_AZ");
	}

	/**
	   * Client and server are using incompatible VEGA builds. [B0]
	   */
	public static String ClientServerDifferentBuilds(boolean symbol) {
		return symbol ? "£B0£":messages.getString("ClientServerDifferentBuilds_B0");
	}

	/**
	   * Data could not be interpreted. Check the following:\n\n1. Is the data coming from a VEGA e-mail?\n2. Is the password correct?\n3. Did you use an e-mail from a wrong context?\n4. Is you VEGA build ({0}) compatible with the build of the sender (see e-mail)?\n\u0009\u0009\u0009\u0009\u0009\u0009 [B1]
	   */
	public static String ClipboardImportErrorPassword(boolean symbol, String arg0) {
		return symbol ? "£B1§"+arg0+"£":MessageFormat.format(messages.getString("ClipboardImportErrorPassword_B1"), arg0);
	}

	/**
	   * Data could not be interpreted. Check the following:\n\n1. Is the data coming from a VEGA e-mail?\n2. Did you use an e-mail from a wrong context?\n3. Your VEGA build ({0}) and the build of the sender (see e-mail) are too different.\n\u0009\u0009\u0009\u0009\u0009\u0009 [B2]
	   */
	public static String ClipboardImportError(boolean symbol, String arg0) {
		return symbol ? "£B2§"+arg0+"£":MessageFormat.format(messages.getString("ClipboardImportError_B2"), arg0);
	}

	/**
	   * Close statistics [B3]
	   */
	public static String CloseStatistics(boolean symbol) {
		return symbol ? "£B3£":messages.getString("CloseStatistics_B3");
	}

	/**
	   * Close [B4]
	   */
	public static String Close(boolean symbol) {
		return symbol ? "£B4£":messages.getString("Close_B4");
	}

	/**
	   * Color [B5]
	   */
	public static String Color(boolean symbol) {
		return symbol ? "£B5£":messages.getString("Color_B5");
	}

	/**
	   * Commander [B6]
	   */
	public static String Commander(boolean symbol) {
		return symbol ? "£B6£":messages.getString("Commander_B6");
	}

	/**
	   * Connect [B7]
	   */
	public static String Connect(boolean symbol) {
		return symbol ? "£B7£":messages.getString("Connect_B7");
	}

	/**
	   * Connected VEGA Display clients [B8]
	   */
	public static String ConnectedVegaDisplayClients(boolean symbol) {
		return symbol ? "£B8£":messages.getString("ConnectedVegaDisplayClients_B8");
	}

	/**
	   * Connected with server {0} [B9]
	   */
	public static String ConnectedWithServer(boolean symbol, String arg0) {
		return symbol ? "£B9§"+arg0+"£":MessageFormat.format(messages.getString("ConnectedWithServer_B9"), arg0);
	}

	/**
	   * Connected as {0} [BA]
	   */
	public static String ConnectedWithVegaServer(boolean symbol, String arg0) {
		return symbol ? "£BA§"+arg0+"£":MessageFormat.format(messages.getString("ConnectedWithVegaServer_BA"), arg0);
	}

	/**
	   * Connection error [BB]
	   */
	public static String ConnectionError(boolean symbol) {
		return symbol ? "£BB£":messages.getString("ConnectionError_BB");
	}

	/**
	   * Connection settings [BC]
	   */
	public static String ConnectionSettings(boolean symbol) {
		return symbol ? "£BC£":messages.getString("ConnectionSettings_BC");
	}

	/**
	   * Connection status [BD]
	   */
	public static String ConnectionStatus(boolean symbol) {
		return symbol ? "£BD£":messages.getString("ConnectionStatus_BD");
	}

	/**
	   * Connection successful [BE]
	   */
	public static String ConnectionSuccessful(boolean symbol) {
		return symbol ? "£BE£":messages.getString("ConnectionSuccessful_BE");
	}

	/**
	   * Connection test [BF]
	   */
	public static String ConnectionTest(boolean symbol) {
		return symbol ? "£BF£":messages.getString("ConnectionTest_BF");
	}

	/**
	   * No connection to the VEGA server. [BG]
	   */
	public static String ConnectionToServerNotEstablished(boolean symbol) {
		return symbol ? "£BG£":messages.getString("ConnectionToServerNotEstablished_BG");
	}

	/**
	   * Data was copied to the clipboard. [BH]
	   */
	public static String CopiedToClipboard(boolean symbol) {
		return symbol ? "£BH£":messages.getString("CopiedToClipboard_BH");
	}

	/**
	   * Copy to clipboard [BI]
	   */
	public static String CopyToClipboard(boolean symbol) {
		return symbol ? "£BI£":messages.getString("CopyToClipboard_BI");
	}

	/**
	   * Count [BJ]
	   */
	public static String Count(boolean symbol) {
		return symbol ? "£BJ£":messages.getString("Count_BJ");
	}

	/**
	   * Create e-mail [BK]
	   */
	public static String CreateEmail(boolean symbol) {
		return symbol ? "£BK£":messages.getString("CreateEmail_BK");
	}

	/**
	   * Create new user [BL]
	   */
	public static String CreateNewUser(boolean symbol) {
		return symbol ? "£BL£":messages.getString("CreateNewUser_BL");
	}

	/**
	   * Do you really want to create user [{0}]? [BM]
	   */
	public static String CreateUserQuestion(boolean symbol, String arg0) {
		return symbol ? "£BM§"+arg0+"£":MessageFormat.format(messages.getString("CreateUserQuestion_BM"), arg0);
	}

	/**
	   * Current allies [BN]
	   */
	public static String CurrentAllies(boolean symbol) {
		return symbol ? "£BN£":messages.getString("CurrentAllies_BN");
	}

	/**
	   * {1}/{0}/{2} [BO]
	   */
	public static String DateFormatted(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£BO§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("DateFormatted_BO"), arg0, arg1, arg2);
	}

	/**
	   * Day {0} of {1} [BP]
	   */
	public static String DayOf(boolean symbol, String arg0, String arg1) {
		return symbol ? "£BP§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("DayOf_BP"), arg0, arg1);
	}

	/**
	   * default [BQ]
	   */
	public static String Default(boolean symbol) {
		return symbol ? "£BQ£":messages.getString("Default_BQ");
	}

	/**
	   * DBs [BR]
	   */
	public static String DefensiveBattleshipsShort(boolean symbol) {
		return symbol ? "£BR£":messages.getString("DefensiveBattleshipsShort_BR");
	}

	/**
	   * Def. b'ships / combat strength [BS]
	   */
	public static String DefensiveBattleships(boolean symbol) {
		return symbol ? "£BS£":messages.getString("DefensiveBattleships_BS");
	}

	/**
	   * Do you really want to delete the game {0} from the server? [BT]
	   */
	public static String DeleteGameQuestion(boolean symbol, String arg0) {
		return symbol ? "£BT§"+arg0+"£":MessageFormat.format(messages.getString("DeleteGameQuestion_BT"), arg0);
	}

	/**
	   * Delete game [BU]
	   */
	public static String DeleteGame(boolean symbol) {
		return symbol ? "£BU£":messages.getString("DeleteGame_BU");
	}

	/**
	   * Do you really want to delete user [{0}] from the server? This action cannot be made undone! [BV]
	   */
	public static String DeleteUserQuestion(boolean symbol, String arg0) {
		return symbol ? "£BV§"+arg0+"£":MessageFormat.format(messages.getString("DeleteUserQuestion_BV"), arg0);
	}

	/**
	   * Delete user [BW]
	   */
	public static String DeleteUser(boolean symbol) {
		return symbol ? "£BW£":messages.getString("DeleteUser_BW");
	}

	/**
	   * Delete [BX]
	   */
	public static String Delete(boolean symbol) {
		return symbol ? "£BX£":messages.getString("Delete_BX");
	}

	/**
	   * Destination of a transfer must be a planet. [BY]
	   */
	public static String DestinationOfTransferMustBePlanet(boolean symbol) {
		return symbol ? "£BY£":messages.getString("DestinationOfTransferMustBePlanet_BY");
	}

	/**
	   * Destination planet [BZ]
	   */
	public static String DestinationPlanet(boolean symbol) {
		return symbol ? "£BZ£":messages.getString("DestinationPlanet_BZ");
	}

	/**
	   * Destination sector/planet [C0]
	   */
	public static String DestinationSectorOrPlanet(boolean symbol) {
		return symbol ? "£C0£":messages.getString("DestinationSectorOrPlanet_C0");
	}

	/**
	   * Dest [C1]
	   */
	public static String DestinationShort(boolean symbol) {
		return symbol ? "£C1£":messages.getString("DestinationShort_C1");
	}

	/**
	   * Display alliance structure on planet [C2]
	   */
	public static String DisplayAllianceOnPlanet(boolean symbol) {
		return symbol ? "£C2£":messages.getString("DisplayAllianceOnPlanet_C2");
	}

	/**
	   * Distances in light years [C3]
	   */
	public static String DistanceMatrixHeader(boolean symbol) {
		return symbol ? "£C3£":messages.getString("DistanceMatrixHeader_C3");
	}

	/**
	   * Distance matrix [C4]
	   */
	public static String DistanceMatrix(boolean symbol) {
		return symbol ? "£C4£":messages.getString("DistanceMatrix_C4");
	}

	/**
	   * Do you really want to quit VEGA? [C5]
	   */
	public static String DoYouWantToQuitVega(boolean symbol) {
		return symbol ? "£C5£":messages.getString("DoYouWantToQuitVega_C5");
	}

	/**
	   * Download log [C6]
	   */
	public static String DownloadLog(boolean symbol) {
		return symbol ? "£C6£":messages.getString("DownloadLog_C6");
	}

	/**
	   * The name {0} is assigned to more than one player. [C7]
	   */
	public static String DuplicatePlayers(boolean symbol, String arg0) {
		return symbol ? "£C7§"+arg0+"£":MessageFormat.format(messages.getString("DuplicatePlayers_C7"), arg0);
	}

	/**
	   * E-mail actions [C8]
	   */
	public static String EmailActions(boolean symbol) {
		return symbol ? "£C8£":messages.getString("EmailActions_C8");
	}

	/**
	   * The e-mail address of the game host is invalid. [C9]
	   */
	public static String EmailAddressGameHostInvalid(boolean symbol) {
		return symbol ? "£C9£":messages.getString("EmailAddressGameHostInvalid_C9");
	}

	/**
	   * The e-mail address of player [{0}] is invalid. [CA]
	   */
	public static String EmailAddressInvalid(boolean symbol, String arg0) {
		return symbol ? "£CA§"+arg0+"£":MessageFormat.format(messages.getString("EmailAddressInvalid_CA"), arg0);
	}

	/**
	   * (E-mail address unknown) [CB]
	   */
	public static String EmailAddressUnknown(boolean symbol) {
		return symbol ? "£CB£":messages.getString("EmailAddressUnknown_CB");
	}

	/**
	   * E-mail address [CC]
	   */
	public static String EmailAddress(boolean symbol) {
		return symbol ? "£CC£":messages.getString("EmailAddress_CC");
	}

	/**
	   * E-mail address of the administrator [CD]
	   */
	public static String EmailAdmin(boolean symbol) {
		return symbol ? "£CD£":messages.getString("EmailAdmin_CD");
	}

	/**
	   * Hello,\n\nwelcome to VEGA! {0} invited you to the game {1} on server {2}:{3}.\n\nHave fun!\nYour game host [CE]
	   */
	public static String EmailBodyInvitation(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£CE§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("EmailBodyInvitation_CE"), arg0, arg1, arg2, arg3);
	}

	/**
	   * Game: {0}\nYear: {1}\nMoves of: {2}\nBuild: {3}\n\nDear game host,\n\nhere are the moves of {4}. Please proceed as follows:\n\n1. Select this whole e-mail text (for example, with ctrl + A), and copy it to the clipboard of your computer (for example, with ctrl + C).\n\n2. Start VEGA. Load the local game {5}.vega\n\n3. Go to the main menu. Select "E-mail actions > Import moves of a player".\n\n4. Press the button "Insert" to insert the contents of the clipboard into the text field.\n\n5. Press the button "OK". [CF]
	   */
	public static String EmailGameEmailBodyMoves(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		return symbol ? "£CF§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"§"+arg5+"£":MessageFormat.format(messages.getString("EmailGameEmailBodyMoves_CF"), arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/**
	   * Game: {0}\nYear: {1}\nBuild: {2}\n\nHi {3},\n\nthe game has been evaluated. Please proceed with entering your moves.\n\n1. Select this whole e-mail text (for example, with ctrl + A), and copy it to the clipboard of your computer (for example, with ctrl + C).\n\n2. Start VEGA and select  "Import e-mail game from clipboard" in the menu list.\n\n3. Press the button "Insert" to copy the contents of the clipboard into the text field.\n\n4. Press the button "OK".\n\nNow enter your moves. When you are finished, an e-mail opens automatically in your e-mail client. This e-mail contains your moves and the address of the game host. Please send it without changes.\n\nThanks!\nYou game host [CG]
	   */
	public static String EmailGameEmailBody(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£CG§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("EmailGameEmailBody_CG"), arg0, arg1, arg2, arg3);
	}

	/**
	   * E-mail mode settings [CH]
	   */
	public static String EmailModeSettings(boolean symbol) {
		return symbol ? "£CH£":messages.getString("EmailModeSettings_CH");
	}

	/**
	   * E-mail mode [CI]
	   */
	public static String EmailMode(boolean symbol) {
		return symbol ? "£CI£":messages.getString("EmailMode_CI");
	}

	/**
	   * E-mail client cannot be opened:\n{0} [CJ]
	   */
	public static String EmailOpenError(boolean symbol, String arg0) {
		return symbol ? "£CJ§"+arg0+"£":MessageFormat.format(messages.getString("EmailOpenError_CJ"), arg0);
	}

	/**
	   * E-mail settings [CK]
	   */
	public static String EmailSettings(boolean symbol) {
		return symbol ? "£CK£":messages.getString("EmailSettings_CK");
	}

	/**
	   * [VEGA] {0} invited you to the new game {1} [CL]
	   */
	public static String EmailSubjectInvitation(boolean symbol, String arg0, String arg1) {
		return symbol ? "£CL§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("EmailSubjectInvitation_CL"), arg0, arg1);
	}

	/**
	   * [VEGA] Your new user [{0}] [CM]
	   */
	public static String EmailSubjectNewUser(boolean symbol, String arg0) {
		return symbol ? "£CM§"+arg0+"£":MessageFormat.format(messages.getString("EmailSubjectNewUser_CM"), arg0);
	}

	/**
	   * An e-mail was created in your standard e-mail client. [CN]
	   */
	public static String EmailWasCreatedInStandardClient(boolean symbol) {
		return symbol ? "£CN£":messages.getString("EmailWasCreatedInStandardClient_CN");
	}

	/**
	   * E-mail [CO]
	   */
	public static String Email(boolean symbol) {
		return symbol ? "£CO£":messages.getString("Email_CO");
	}

	/**
	   * {0} e-mails were created in your standard e-mail client. [CP]
	   */
	public static String EmailsWereCreated(boolean symbol, String arg0) {
		return symbol ? "£CP§"+arg0+"£":MessageFormat.format(messages.getString("EmailsWereCreated_CP"), arg0);
	}

	/**
	   * End of the year [CQ]
	   */
	public static String EndOfYear(boolean symbol) {
		return symbol ? "£CQ£":messages.getString("EndOfYear_CQ");
	}

	/**
	   * Enter alliance members [CR]
	   */
	public static String EnterAllianceMembers(boolean symbol) {
		return symbol ? "£CR£":messages.getString("EnterAllianceMembers_CR");
	}

	/**
	   * Enter moves [CS]
	   */
	public static String EnterMoves(boolean symbol) {
		return symbol ? "£CS£":messages.getString("EnterMoves_CS");
	}

	/**
	   * Are all your entries correct? Yes = [1] / No = [other key] [CT]
	   */
	public static String EntriesCorrectQuestion(boolean symbol) {
		return symbol ? "£CT£":messages.getString("EntriesCorrectQuestion_CT");
	}

	/**
	   * Error [CU]
	   */
	public static String Error(boolean symbol) {
		return symbol ? "£CU£":messages.getString("Error_CU");
	}

	/**
	   * The evaluation begins. [CV]
	   */
	public static String EvaluationBegins(boolean symbol) {
		return symbol ? "£CV£":messages.getString("EvaluationBegins_CV");
	}

	/**
	   * End of evaluation. [CW]
	   */
	public static String EvaluationEnd(boolean symbol) {
		return symbol ? "£CW£":messages.getString("EvaluationEnd_CW");
	}

	/**
	   * Evaluation [CX]
	   */
	public static String Evaluation(boolean symbol) {
		return symbol ? "£CX£":messages.getString("Evaluation_CX");
	}

	/**
	   * The attacker loses. [CY]
	   */
	public static String FightSimulationAttackNoSuccess(boolean symbol) {
		return symbol ? "£CY£":messages.getString("FightSimulationAttackNoSuccess_CY");
	}

	/**
	   * The attacker wins. [CZ]
	   */
	public static String FightSimulationAttackSuccess(boolean symbol) {
		return symbol ? "£CZ£":messages.getString("FightSimulationAttackSuccess_CZ");
	}

	/**
	   * Attacker [D0]
	   */
	public static String FightSimulationAttackerCount(boolean symbol) {
		return symbol ? "£D0£":messages.getString("FightSimulationAttackerCount_D0");
	}

	/**
	   * Defender [D2]
	   */
	public static String FightSimulationPlanetCount(boolean symbol) {
		return symbol ? "£D2£":messages.getString("FightSimulationPlanetCount_D2");
	}

	/**
	   * Fight simulation [D4]
	   */
	public static String FightSimulation(boolean symbol) {
		return symbol ? "£D4£":messages.getString("FightSimulation_D4");
	}

	/**
	   * The file {0}\ndoes not contain valid credentials. [D6]
	   */
	public static String FileContainsInvalidCredentials(boolean symbol, String arg0) {
		return symbol ? "£D6§"+arg0+"£":MessageFormat.format(messages.getString("FileContainsInvalidCredentials_D6"), arg0);
	}

	/**
	   * VEGA files [D7]
	   */
	public static String FileFilterDescription(boolean symbol) {
		return symbol ? "£D7£":messages.getString("FileFilterDescription_D7");
	}

	/**
	   * File does not exist. [D8]
	   */
	public static String FileNotExists(boolean symbol) {
		return symbol ? "£D8£":messages.getString("FileNotExists_D8");
	}

	/**
	   * This file is not a valid VEGA game file. [D9]
	   */
	public static String FileNotValid(boolean symbol) {
		return symbol ? "£D9£":messages.getString("FileNotValid_D9");
	}

	/**
	   * Do you really want to finalize the game? [DB]
	   */
	public static String FinalizeGameQuestion(boolean symbol) {
		return symbol ? "£DB£":messages.getString("FinalizeGameQuestion_DB");
	}

	/**
	   * Do you really want to finalize the game {0} immediately? All moves from the playes will get lost. [DC]
	   */
	public static String FinalizeGameQuestion(boolean symbol, String arg0) {
		return symbol ? "£DC§"+arg0+"£":MessageFormat.format(messages.getString("FinalizeGameQuestion_DC"), arg0);
	}

	/**
	   * Finalize game [DD]
	   */
	public static String FinalizeGame(boolean symbol) {
		return symbol ? "£DD£":messages.getString("FinalizeGame_DD");
	}

	/**
	   * Finalize [DE]
	   */
	public static String Finalize(boolean symbol) {
		return symbol ? "£DE£":messages.getString("Finalize_DE");
	}

	/**
	   * Finalized game in year {0} [DF]
	   */
	public static String FinalizedGameInYear(boolean symbol, String arg0) {
		return symbol ? "£DF§"+arg0+"£":MessageFormat.format(messages.getString("FinalizedGameInYear_DF"), arg0);
	}

	/**
	   * $ prod. [DG]
	   */
	public static String FinalizedGameMoneyProduction(boolean symbol) {
		return symbol ? "£DG£":messages.getString("FinalizedGameMoneyProduction_DG");
	}

	/**
	   * Position {0}: [DH]
	   */
	public static String FinalizedGamePosition(boolean symbol, String arg0) {
		return symbol ? "£DH§"+arg0+"£":MessageFormat.format(messages.getString("FinalizedGamePosition_DH"), arg0);
	}

	/**
	   * You will find the finalized game under "My server-based games" soon. [DI]
	   */
	public static String FinalizedGameUnderMyServerBasedGames(boolean symbol) {
		return symbol ? "£DI£":messages.getString("FinalizedGameUnderMyServerBasedGames_DI");
	}

	/**
	   * Finalized games ({0}) [DJ]
	   */
	public static String FinalizedGames(boolean symbol, String arg0) {
		return symbol ? "£DJ§"+arg0+"£":MessageFormat.format(messages.getString("FinalizedGames_DJ"), arg0);
	}

	/**
	   * Do you want to finish entering moves? [DK]
	   */
	public static String FinishEnterMovesQuestion(boolean symbol) {
		return symbol ? "£DK£":messages.getString("FinishEnterMovesQuestion_DK");
	}

	/**
	   * Finish [DL]
	   */
	public static String FinishEnterMoves(boolean symbol) {
		return symbol ? "£DL£":messages.getString("FinishEnterMoves_DL");
	}

	/**
	   * Finish [DM]
	   */
	public static String Finish(boolean symbol) {
		return symbol ? "£DM£":messages.getString("Finish_DM");
	}

	/**
	   * For. Spies [DN]
	   */
	public static String ForeignSpies(boolean symbol) {
		return symbol ? "£DN£":messages.getString("ForeignSpies_DN");
	}

	/**
	   * Freight [DO]
	   */
	public static String Freight(boolean symbol) {
		return symbol ? "£DO£":messages.getString("Freight_DO");
	}

	/**
	   * Fr [DP]
	   */
	public static String FromShort(boolean symbol) {
		return symbol ? "£DP£":messages.getString("FromShort_DP");
	}

	/**
	   * The new game [{0}] was created on the server. You may send an invitation e-mail to the other players after closing this message. [DQ]
	   */
	public static String GameCreatedSendMail(boolean symbol, String arg0) {
		return symbol ? "£DQ§"+arg0+"£":MessageFormat.format(messages.getString("GameCreatedSendMail_DQ"), arg0);
	}

	/**
	   * The new game [{0}] was created on the server. [DR]
	   */
	public static String GameCreated(boolean symbol, String arg0) {
		return symbol ? "£DR§"+arg0+"£":MessageFormat.format(messages.getString("GameCreated_DR"), arg0);
	}

	/**
	   * Game {0} was deleted successfully. [DS]
	   */
	public static String GameDeletedSuccessfully(boolean symbol, String arg0) {
		return symbol ? "£DS§"+arg0+"£":MessageFormat.format(messages.getString("GameDeletedSuccessfully_DS"), arg0);
	}

	/**
	   * Game {0} was finalized successfully. [DT]
	   */
	public static String GameFinalizedSuccessfully(boolean symbol, String arg0) {
		return symbol ? "£DT§"+arg0+"£":MessageFormat.format(messages.getString("GameFinalizedSuccessfully_DT"), arg0);
	}

	/**
	   * The game has already been finalized. [DU]
	   */
	public static String GameHasBeenFinalized(boolean symbol) {
		return symbol ? "£DU£":messages.getString("GameHasBeenFinalized_DU");
	}

	/**
	   * Game host [DV]
	   */
	public static String GameHost(boolean symbol) {
		return symbol ? "£DV£":messages.getString("GameHost_DV");
	}

	/**
	   * Game info [DW]
	   */
	public static String GameInfo(boolean symbol) {
		return symbol ? "£DW£":messages.getString("GameInfo_DW");
	}

	/**
	   * The game name [{0}] ist invalid.\nThe length of a game name must be between {1} and {2} characters.\nIt must only contain the characters a-z, A-Z, and 0-9. [DX]
	   */
	public static String GameNameInvalid(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£DX§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("GameNameInvalid_DX"), arg0, arg1, arg2);
	}

	/**
	   * The game [{0}] does not exist! [DY]
	   */
	public static String GameNotExists(boolean symbol, String arg0) {
		return symbol ? "£DY§"+arg0+"£":MessageFormat.format(messages.getString("GameNotExists_DY"), arg0);
	}

	/**
	   * Game parameters [DZ]
	   */
	public static String GameParameters(boolean symbol) {
		return symbol ? "£DZ£":messages.getString("GameParameters_DZ");
	}

	/**
	   * Game started on [E0]
	   */
	public static String GameStartedOn(boolean symbol) {
		return symbol ? "£E0£":messages.getString("GameStartedOn_E0");
	}

	/**
	   * A game with the same name already exists! [E1]
	   */
	public static String GameWithSameNameExists(boolean symbol) {
		return symbol ? "£E1£":messages.getString("GameWithSameNameExists_E1");
	}

	/**
	   * Games on the VEGA server [E2]
	   */
	public static String GamesOnServer(boolean symbol) {
		return symbol ? "£E2£":messages.getString("GamesOnServer_E2");
	}

	/**
	   * Games on the VEGA server (user {0}) [E3]
	   */
	public static String GamesOnVegaServer(boolean symbol, String arg0) {
		return symbol ? "£E3§"+arg0+"£":MessageFormat.format(messages.getString("GamesOnVegaServer_E3"), arg0);
	}

	/**
	   * Get [E4]
	   */
	public static String GetIp(boolean symbol) {
		return symbol ? "£E4£":messages.getString("GetIp_E4");
	}

	/**
	   * Hide/show spaceships [E5]
	   */
	public static String HideOrShowSpaceships(boolean symbol) {
		return symbol ? "£E5£":messages.getString("HideOrShowSpaceships_E5");
	}

	/**
	   * User is active [E6]
	   */
	public static String UserIsActive(boolean symbol) {
		return symbol ? "£E6£":messages.getString("UserIsActive_E6");
	}

	/**
	   * Count [E7]
	   */
	public static String CountShort(boolean symbol) {
		return symbol ? "£E7£":messages.getString("CountShort_E7");
	}

	/**
	   * Active Spies [E8]
	   */
	public static String ActiveSpies(boolean symbol) {
		return symbol ? "£E8£":messages.getString("ActiveSpies_E8");
	}

	/**
	   * The neutral fleet has conquered the planet. [E9]
	   */
	public static String PlanetConqueredNeutral(boolean symbol) {
		return symbol ? "£E9£":messages.getString("PlanetConqueredNeutral_E9");
	}

	/**
	   * Load server data [EA]
	   */
	public static String LoadServerData(boolean symbol) {
		return symbol ? "£EA£":messages.getString("LoadServerData_EA");
	}

	/**
	   * {0} bytes [EB]
	   */
	public static String Bytes(boolean symbol, String arg0) {
		return symbol ? "£EB§"+arg0+"£":MessageFormat.format(messages.getString("Bytes_EB"), arg0);
	}

	/**
	   * Inactive user from the clipboard [EC]
	   */
	public static String InactiveUserFromClipboard(boolean symbol) {
		return symbol ? "£EC£":messages.getString("InactiveUserFromClipboard_EC");
	}

	/**
	   * Active user from a file [ED]
	   */
	public static String ActiveUserFromFile(boolean symbol) {
		return symbol ? "£ED£":messages.getString("ActiveUserFromFile_ED");
	}

	/**
	   * Add credentials [EE]
	   */
	public static String AddCredentials(boolean symbol) {
		return symbol ? "£EE£":messages.getString("AddCredentials_EE");
	}

	/**
	   * Delete credentials [EF]
	   */
	public static String DeleteCredentials(boolean symbol) {
		return symbol ? "£EF£":messages.getString("DeleteCredentials_EF");
	}

	/**
	   * Do you really want to delete the credentials of user [{0}]? [EG]
	   */
	public static String DeleteCredentialsAYS(boolean symbol, String arg0) {
		return symbol ? "£EG§"+arg0+"£":MessageFormat.format(messages.getString("DeleteCredentialsAYS_EG"), arg0);
	}

	/**
	   * Server credentials are locked. [EH]
	   */
	public static String ServerCredentialsLocked(boolean symbol) {
		return symbol ? "£EH£":messages.getString("ServerCredentialsLocked_EH");
	}

	/**
	   * Unlock server credentials [EI]
	   */
	public static String UnlockServerCredentials(boolean symbol) {
		return symbol ? "£EI£":messages.getString("UnlockServerCredentials_EI");
	}

	/**
	   * Change password for server credentials [EJ]
	   */
	public static String ChangeServerCredentialsPasswort(boolean symbol) {
		return symbol ? "£EJ£":messages.getString("ChangeServerCredentialsPasswort_EJ");
	}

	/**
	   * Set new password for server credentials [EK]
	   */
	public static String SetServerCredentialsPasswort(boolean symbol) {
		return symbol ? "£EK£":messages.getString("SetServerCredentialsPasswort_EK");
	}

	/**
	   * Old password [EL]
	   */
	public static String OldPassword(boolean symbol) {
		return symbol ? "£EL£":messages.getString("OldPassword_EL");
	}

	/**
	   * New password [EM]
	   */
	public static String NewPassword(boolean symbol) {
		return symbol ? "£EM£":messages.getString("NewPassword_EM");
	}

	/**
	   * New password (repeat) [EN]
	   */
	public static String NewPasswordRepeat(boolean symbol) {
		return symbol ? "£EN£":messages.getString("NewPasswordRepeat_EN");
	}

	/**
	   * The password is wrong. [EO]
	   */
	public static String PasswordWrong(boolean symbol) {
		return symbol ? "£EO£":messages.getString("PasswordWrong_EO");
	}

	/**
	   * Wrong password [EP]
	   */
	public static String PasswordWrong2(boolean symbol) {
		return symbol ? "£EP£":messages.getString("PasswordWrong2_EP");
	}

	/**
	   * Invalid password [EQ]
	   */
	public static String PasswordInvalid(boolean symbol) {
		return symbol ? "£EQ£":messages.getString("PasswordInvalid_EQ");
	}

	/**
	   * The new password must have at least {0} characters. [ER]
	   */
	public static String PasswordLength(boolean symbol, String arg0) {
		return symbol ? "£ER§"+arg0+"£":MessageFormat.format(messages.getString("PasswordLength_ER"), arg0);
	}

	/**
	   * Change password [ES]
	   */
	public static String ChangePassword(boolean symbol) {
		return symbol ? "£ES£":messages.getString("ChangePassword_ES");
	}

	/**
	   * Delete credentials [ET]
	   */
	public static String ClearServerCredentials(boolean symbol) {
		return symbol ? "£ET£":messages.getString("ClearServerCredentials_ET");
	}

	/**
	   * Attention! All server credentials and messages will be deleted! [EU]
	   */
	public static String ClearServerCredentialsAys(boolean symbol) {
		return symbol ? "£EU£":messages.getString("ClearServerCredentialsAys_EU");
	}

	/**
	   * The admin credentials for the server were saved in the file\n\n{0}\n\nStart VEGA and import the credentials under Settings > VEGA server settings >\nCredentials > + > Active user from a file. After that, you should delete\nthe credentials file for security reasons. [EV]
	   */
	public static String ServerAdminCredentialsCreated(boolean symbol, String arg0) {
		return symbol ? "£EV§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminCredentialsCreated_EV"), arg0);
	}

	/**
	   * Unsent messages [EW]
	   */
	public static String UnsentMessages(boolean symbol) {
		return symbol ? "£EW£":messages.getString("UnsentMessages_EW");
	}

	/**
	   * There are unsent messages. Do you really want to close the messenger? [EX]
	   */
	public static String UnsentMessages2(boolean symbol) {
		return symbol ? "£EX£":messages.getString("UnsentMessages2_EX");
	}

	/**
	   * New conversation [EY]
	   */
	public static String ConversationNew(boolean symbol) {
		return symbol ? "£EY£":messages.getString("ConversationNew_EY");
	}

	/**
	   * Delete conversation [EZ]
	   */
	public static String ConversationDelete(boolean symbol) {
		return symbol ? "£EZ£":messages.getString("ConversationDelete_EZ");
	}

	/**
	   * Hi {0},\n\nwelcome to VEGA! Your new user [{1}] on server {2}:{3} (build {4}) has been created and only needs to be activated.\n\nPlease proceed as follows:\n\n1. Select this whole text of this file (for example, with ctrl + A), and copy it to the clipboard of your computer (for example, with ctrl + C).\n\n2. Start VEGA and select "Settings > VEGA server settings -> Credentials -> + -> Inactive user from the clipboard".\n\n3. Insert the contents of the clipboard into the text field and enter the password that you agreed on with your server administrator. Prss "OK".\n\n4. Activate the user with the button "Activate".\n\n5. Select your user from the drop-down list next to "Connect as player with the credentials" and set the checkbox.\n\n6. Close the dialog by pressing "OK".\n\nYour user is now active.\n\nEnjoy VEGA!\nYour server administrator\n\n{5} [F0]
	   */
	public static String CredentialsTextFileInstructions(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		return symbol ? "£F0§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"§"+arg5+"£":MessageFormat.format(messages.getString("CredentialsTextFileInstructions_F0"), arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/**
	   * Attacker: {0} [{1}] -> {2} [{3}] [F1]
	   */
	public static String FightAttacker(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£F1§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("FightAttacker_F1"), arg0, arg1, arg2, arg3);
	}

	/**
	   * Defender: {0} [{1}] -> {2} [{3}] [F2]
	   */
	public static String FightDefender(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£F2§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("FightDefender_F2"), arg0, arg1, arg2, arg3);
	}

	/**
	   * ![size=1.5;color=1;bold|Launch more battleships (2)]\n\n![color=1|Task]: ![italic;color=1|Send 80 battleships from your planet LM to planet LG.] [F3]
	   */
	public static String TutorialText07a(boolean symbol) {
		return symbol ? "£F3£":messages.getString("TutorialText07a_F3");
	}

	/**
	   * ComS [F5]
	   */
	public static String CombatStrengthShort(boolean symbol) {
		return symbol ? "£F5£":messages.getString("CombatStrengthShort_F5");
	}

	/**
	   * ![size=1.5;color=1;bold|Conquer and defend planets]\n\nSend battleships to conquer a foreign planet. You fight against all battleships on the planet, including the defensive battleships. The outcome of the battle is very easy to calculate:\n\nThe defender loses the number of attacking battleships, the attacker loses the number of defending battleships. If there are any attacker ships left, the attacker takes over the planet.\n\nExample:\n\nAttacker: 100\nDefender:  75\n\nAfter the fight:\n\nAttacker: 100 -  75 = 25\nDefender:  75 - 100 <  0\n\nIn this example, the attacker takes over the planet with 25 ships. [F6]
	   */
	public static String TutorialText04a(boolean symbol) {
		return symbol ? "£F6£":messages.getString("TutorialText04a_F6");
	}

	/**
	   * ![size=1.5;color=1;bold|The combat bonus]\n\nYou can increase the combat strength of a planet from 100% to 150% and 200%. When the planet is attacked, the strength of all battleships on the planet \u2013 including defensive battleships and battleships of allied partners \u2013 is multiplied by 1.5 and 2 respectively. 100 battleships therefore act like 150 or 200 battleships. When you launch an attack on another planet from one planet, the battleships take on the combat strength of the starting planet.\n\nWith a higher combat strength, you can inflict more damage on the opponent while reducing your own losses. If both parties have the same combat strength, the effect is canceled out. [F7]
	   */
	public static String TutorialText04b(boolean symbol) {
		return symbol ? "£F7£":messages.getString("TutorialText04b_F7");
	}

	/**
	   * Error when opening the game manual file {0}.\n\n{1} [F8]
	   */
	public static String VegaHelpError(boolean symbol, String arg0, String arg1) {
		return symbol ? "£F8§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("VegaHelpError_F8"), arg0, arg1);
	}

	/**
	   * Combat strength [F9]
	   */
	public static String FightSimulationCombatStrength(boolean symbol) {
		return symbol ? "£F9£":messages.getString("FightSimulationCombatStrength_F9");
	}

	/**
	   * Combat strength (+{0}%) [FA]
	   */
	public static String BuySellCombatStrength(boolean symbol, String arg0) {
		return symbol ? "£FA§"+arg0+"£":MessageFormat.format(messages.getString("BuySellCombatStrength_FA"), arg0);
	}
}