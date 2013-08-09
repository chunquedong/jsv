package chunquedong.jsv.record.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SerializeUtil {
	public static boolean write(OutputStream out, List<Record> set) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeInt(set.size());
			if (set.size() == 0) {
				return false;
			}
			Record r0 = set.get(0);
			Schema table = r0.getSchema();
			oos.writeObject(table);
			
			for (Record r : set) {
				if (r.getSchema() != table) {
					throw new RuntimeException("schema discord");
				}
				oos.writeObject(r);
			}
			oos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static List<Record> read(InputStream in) {
		try {
			ObjectInputStream ois = new ObjectInputStream(in);
	    int size = ois.readInt();
	    ArrayList<Record> list = null;
	    if (size == 0) {
	    	return new ArrayList<Record>();
	    } else {
	    	list = new ArrayList<Record>(size);
	    }
	    
	    Schema nt = (Schema)ois.readObject();
	    nt.resetMap();
	    
	    for (int i=0; i<size; ++i) {
	    	Record r = (Record)ois.readObject();
	    	r._setSchema(nt);
	    	list.add(r);
	    }
	    ois.close();
	    return list;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
	    e.printStackTrace();
    }
		return null;
	}
}
