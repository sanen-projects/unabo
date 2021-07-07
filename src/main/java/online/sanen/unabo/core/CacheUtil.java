package online.sanen.unabo.core;

import java.util.HashMap;
import java.util.Map;

import com.mhdt.structure.cache.Cache;
import com.mhdt.structure.cache.LRUCache;

/**
 * 
 * @author online.sanen 
 * Date： 2017/11/22 
 * Time: 20:34
 *
 */
public class CacheUtil {
	
	Map<String,Cache<String,Object>> caches = new HashMap<>();
	
	
	private CacheUtil() {
		
	}

	static CacheUtil cacheUtil;
	
	public static CacheUtil getInstance() {
		if(cacheUtil == null)
			cacheUtil =  new CacheUtil();
		
		return cacheUtil;
	}

	public  Cache<String,Object> getCache(String cacheName) {
		
		if(!caches.containsKey(cacheName))
			caches.put(cacheName, new LRUCache<String, Object>(1000));
		
		return caches.get(cacheName);
	}

	public Object get(String cacheName, String key) {
		
		return this.getCache(cacheName).get(key);
	}
	
	
	
}