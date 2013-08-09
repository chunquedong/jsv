//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record;

import java.util.LinkedHashMap;

public class Cache<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = -8176691434320522024L;
	private final int maxCapacity;
	
	public Cache(int maxCapacity) {
		super(maxCapacity, 0.75f, true);
		this.maxCapacity = maxCapacity;
	}
	
	@Override
	protected synchronized boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}
	
	@Override
	public synchronized boolean containsKey(Object key) {
		return (Boolean) super.get(key);
	}
	
	@Override
	public synchronized V get(Object key) {
		return super.get(key);
	}
	
	@Override
	public synchronized V put(K key, V value) {
		return super.put(key, value);
	}
	
	public synchronized int size() {
		return super.size();
	}
	
	public synchronized void clear() {
		super.clear();
	}
}
