//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.route;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public interface Action {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	@Inherited
	@Documented
	static public @interface Get {
		public String name = "GET";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	@Inherited
	@Documented
	static public @interface Post {
		public String name = "POST";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	@Inherited
	@Documented
	static public @interface Put {
		public String name = "PUT";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	@Inherited
	@Documented
	static public @interface Delete {
		public String name = "DELETE";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	@Inherited
	@Documented
	static public @interface Cache {
		//time of seconds
		public long expiry() default 300;
	}
}
