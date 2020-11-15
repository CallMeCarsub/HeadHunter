package tv.tirco.headhunter.database;

public class DatabaseManagerFactory {

	public static DatabaseManager getDatabaseManager() {
		return new PlayerFileManager();
		//return Config.getInstance().getUseMySQL() ? new SQLDatabaseManager() : new PlayerFileManager();
	}

	public static DatabaseManager createDatabaseManager(DatabaseType type) {
		switch (type) {
		case FLATFILE:
			return new PlayerFileManager();

		/*case SQL:
			return new SQLDatabaseManager(); */

		default:
			return null;
		}
	}
}
