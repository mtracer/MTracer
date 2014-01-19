package edu.nudt.xtrace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Report {
	private StringBuilder buf;
	private HashMap<String, List<String>> map; // lazily-built to enhance performance
	public Report() {
		buf = new StringBuilder("X-Trace Report ver 1.0\n");
		map = null;
	}
	private Report(String s) {
		buf = new StringBuilder(s);
		try {
			if (s.charAt(s.length() - 1) != '\n') {
				buf.append("\n");
			}
		} catch (IndexOutOfBoundsException e) {
		}
		map = null;
	}
	public void put(final String key, final String value, boolean append) {
		if (append && map == null) {
			buf.append(key + ": " + value + "\n");
			return;
		}
		if (map == null) {
		convertToMap();
		}
		List<String> values;
		if (map.containsKey(key) && append) {
			values = map.get(key);
		} else {
			values = new ArrayList<String>();
		}

		values.add(value);
		map.put(key, values);
	}

	public void put(final String key, final String value) {
		put(key, value, true);
	}

	public void remove(final String key) {
		convertToMap();
		map.remove(key);
	}

	public List<String> get(String key) {
		convertToMap();
		return map.get(key);
	}

	@Override
	public String toString() {
		if (map == null) {
			return buf.toString();
		}
		StringBuilder buf = new StringBuilder("X-Trace Report ver 1.0\n");
		final Iterator<Map.Entry<String, List<String>>> iter = map.entrySet()
				.iterator();
		while (iter.hasNext()) {
			final Map.Entry<String, List<String>> entry = iter.next();
			final Iterator<String> values = entry.getValue().iterator();
			while (values.hasNext()) {
			final String v = values.next();
				buf.append(entry.getKey() + ": " + v + "\n");
			}
		}
		return buf.toString();
	}

	
	public static Report createFromString(final String s) {
		return new Report(s);
	}

	private void convertToMap() {
		if (map != null) {
			return;
		}
		
		map = new HashMap<String, List<String>>();
		BufferedReader in = new BufferedReader(new StringReader(buf.toString()));
		try {
			String firstLine = in.readLine();
			if (!firstLine.equals("X-Trace Report ver 1.0")) {
				buf = new StringBuilder("X-Trace Report ver 1.0\n");
				map = null;
			}
		} catch (IOException e) {
			buf = new StringBuilder("X-Trace Report ver 1.0\n");
			map = null;
		}

		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				int idx = line.indexOf(":");
				if (idx >= 0) {
					String key = line.substring(0, idx).trim();
					String value = line.substring(idx + 1, line.length()).trim();
					put(key, value);
				}
			}
		} catch (IOException e) {
			buf = new StringBuilder("X-Trace Report ver 1.0\n");
			map = null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Report)) {
			return false;
		} else {
			Report r = (Report) o;
			if (toString().trim().equalsIgnoreCase(r.toString().trim())) {
				return true;
			}
		}
		return false;
	}
}
