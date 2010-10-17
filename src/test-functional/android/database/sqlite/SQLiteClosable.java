package android.database.sqlite;

public abstract class SQLiteClosable {
	public SQLiteClosable() {}

	protected abstract void onAllReferencesReleased();

	protected void onAllReferencesReleasedFromContainer() {
		throw new RuntimeException("Stub!");
	}

	public void acquireReference() {
		throw new RuntimeException("Stub!");
	}

	public void releaseReference() {
		throw new RuntimeException("Stub!");
	}

	public void releaseReferenceFromContainer() {
		throw new RuntimeException("Stub!");
	}
}
