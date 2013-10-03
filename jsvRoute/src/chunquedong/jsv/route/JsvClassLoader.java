package chunquedong.jsv.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

class JsvClassLoader extends ClassLoader {

	private String basedir;
	private HashSet<String> dynaclazns;

	public JsvClassLoader(String basedir, List<String> clazns) {
		super(null);
		this.basedir = basedir;
		dynaclazns = new HashSet<String>();
		for (String className : clazns) {
			dynaclazns.add(className);
		}
	}

	private Class<?> loadDirectly(String name) {
		Class<?> cls = null;
		StringBuffer sb = new StringBuffer(basedir);
		String classname = name.replace('.', File.separatorChar) + ".class";
		sb.append(File.separator + classname);
		File classF = new File(sb.toString());
		try {
			cls = instantiateClass(name, new FileInputStream(classF), classF.length());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cls;
	}

	private Class<?> instantiateClass(String name, InputStream fin, long len)
	    throws IOException {
		byte[] raw = new byte[(int) len];
		fin.read(raw);
		fin.close();
		return defineClass(name, raw, 0, raw.length);
	}

	protected Class<?> loadClass(String name, boolean resolve)
	    throws ClassNotFoundException {
		Class<?> cls = null;
		cls = findLoadedClass(name);
		if (cls == null) {
			if (!this.dynaclazns.contains(name))
				cls = getSystemClassLoader().loadClass(name);
			else
				cls = loadDirectly(name);
			if (cls == null)
				throw new ClassNotFoundException(name);
		}
		if (resolve)
			resolveClass(cls);
		return cls;
	}

}