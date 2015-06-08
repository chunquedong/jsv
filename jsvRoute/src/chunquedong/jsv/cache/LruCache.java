package chunquedong.jsv.cache;

import java.util.LinkedHashMap;

public class LruCache extends LinkedHashMap<String, Cache.CacheItem> implements
		Cache {

	private static final long serialVersionUID = -887516572205463012L;
	private final int maxCapacity;
	
	public LruCache() {
		this(1000);
	}

	public LruCache(int maxCapacity) {
		super(maxCapacity, 0.75f, true);
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected synchronized boolean removeEldestEntry(
			java.util.Map.Entry<String, Cache.CacheItem> eldest) {
		return size() > maxCapacity;
	}

	@Override
	public synchronized Object get(String key) {
		Cache.CacheItem obj = super.get(key);
		if (obj == null) {
			return null;
		}
		
		if (obj.time + (obj.expiry*1000) < System.currentTimeMillis()) {
			remove(key);
			return null;
		}
		return obj.value;
	}

	@Override
	public synchronized Object put(String key, Object value, long expiry) {
		Cache.CacheItem item = new Cache.CacheItem();
		item.key = key;
		item.value = value;
		item.time = System.currentTimeMillis();
		item.expiry = expiry;

		Cache.CacheItem pre = super.put(key, item);
		return pre != null ? pre.value : null;
	}

	@Override
	public synchronized boolean remove(String key) {
		Cache.CacheItem pre = super.remove(key);
		return pre != null;
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}

	@Override
	public synchronized boolean containsKey(String key) {
		return super.containsKey(key);
	}

}
