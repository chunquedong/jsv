package chunquedong.jsv.cache;

public interface Cache {

	public static class CacheItem {
		public String key;
		public Object value;
		public long expiry;
		public long time;
	}
	
	public boolean containsKey(String key);

	public Object get(String key);

	public Object put(String key, Object value, long expiry);

	public boolean remove(String key);

	public void clear();

}
