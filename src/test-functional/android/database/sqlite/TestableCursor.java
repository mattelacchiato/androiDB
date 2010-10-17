package android.database.sqlite;

import java.sql.ResultSet;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

public class TestableCursor implements Cursor {

	private final ResultSet result;

	public TestableCursor(final ResultSet result) {
		this.result = result;
	}

	public void close() {
		throw new RuntimeException("Stub!");

	}

	public void copyStringToBuffer(final int columnIndex, final CharArrayBuffer buffer) {
		throw new RuntimeException("Stub!");

	}

	public void deactivate() {
		throw new RuntimeException("Stub!");

	}

	public byte[] getBlob(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public int getColumnCount() {
		throw new RuntimeException("Stub!");
	}

	public int getColumnIndex(final String columnName) {
		throw new RuntimeException("Stub!");
	}

	public int getColumnIndexOrThrow(final String columnName) throws IllegalArgumentException {
		throw new RuntimeException("Stub!");
	}

	public String getColumnName(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public String[] getColumnNames() {
		throw new RuntimeException("Stub!");
	}

	public int getCount() {
		throw new RuntimeException("Stub!");
	}

	public double getDouble(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public Bundle getExtras() {
		throw new RuntimeException("Stub!");
	}

	public float getFloat(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public int getInt(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public long getLong(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public int getPosition() {
		throw new RuntimeException("Stub!");
	}

	public short getShort(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public String getString(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public boolean getWantsAllOnMoveCalls() {
		throw new RuntimeException("Stub!");
	}

	public boolean isAfterLast() {
		throw new RuntimeException("Stub!");
	}

	public boolean isBeforeFirst() {
		throw new RuntimeException("Stub!");
	}

	public boolean isClosed() {
		throw new RuntimeException("Stub!");
	}

	public boolean isFirst() {
		throw new RuntimeException("Stub!");
	}

	public boolean isLast() {
		throw new RuntimeException("Stub!");
	}

	public boolean isNull(final int columnIndex) {
		throw new RuntimeException("Stub!");
	}

	public boolean move(final int offset) {
		throw new RuntimeException("Stub!");
	}

	public boolean moveToFirst() {
		throw new RuntimeException("Stub!");
	}

	public boolean moveToLast() {
		throw new RuntimeException("Stub!");
	}

	public boolean moveToNext() {
		throw new RuntimeException("Stub!");
	}

	public boolean moveToPosition(final int position) {
		throw new RuntimeException("Stub!");
	}

	public boolean moveToPrevious() {
		throw new RuntimeException("Stub!");
	}

	public void registerContentObserver(final ContentObserver observer) {
		throw new RuntimeException("Stub!");

	}

	public void registerDataSetObserver(final DataSetObserver observer) {
		throw new RuntimeException("Stub!");

	}

	public boolean requery() {
		throw new RuntimeException("Stub!");
	}

	public Bundle respond(final Bundle extras) {
		throw new RuntimeException("Stub!");
	}

	public void setNotificationUri(final ContentResolver cr, final Uri uri) {
		throw new RuntimeException("Stub!");

	}

	public void unregisterContentObserver(final ContentObserver observer) {
		throw new RuntimeException("Stub!");

	}

	public void unregisterDataSetObserver(final DataSetObserver observer) {
		throw new RuntimeException("Stub!");

	}

}
