package util.data;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyMap {

    public static final Pattern CONTEXT_VARS = Pattern.compile("\\{(.+?)\\}");
    public static final Pattern ENV_VARS = Pattern.compile("\\$\\{(.+?)\\}");
    public static final Pattern USER_VARS = Pattern.compile("\\{(%.+?%)\\}");
    
    private static Map<Object,Object> systemVars;
    
    public static String resolveContextVars(String in, Map<?,?> vMap) {
        return replaceKeys(in, CONTEXT_VARS, true, 1, vMap);
    }

    public static String resolveEnvVars(String in) {
        return replaceKeys(in, ENV_VARS);
    }

    /**
     * replace the given pattern with the key-map value
     *
     * @param in input string
     * @param pattern pattern to match
     * @param preserveKeys true to preserve key pattern if its not in key-map
     * @param passes no times to resolve
     * <br> n for n- level of keys (level -> keys inside keys)
     * @param maps key-map list
     * @return resolved string
     */
    public static String replaceKeys(String in, Pattern pattern, boolean preserveKeys, int passes, Map<?,?>... maps) {
        String out = in;
        for (int pass = 1; pass <= passes; pass++) {
            Matcher m = pattern.matcher(in);
            String match, key;
            while (m.find()) {
                match = m.group();
                key = m.group(1);
                Boolean resolved = false;
                if (maps != null) {
                    for (Map<?, ?> map : maps) {
                        if ((resolved = map.containsKey(key))) {
                            out = out.replace(match, Objects.toString(map.get(key)));
                            break;
                        }
                    }
                }
                if (!resolved && !preserveKeys) {
                    out = out.replace(match, key);
                }
            }
            in=out;
        }
        return out;
    }

    /**
     *
     * @param in input string
     * @param p pattern to match
     * @return resolved string
     */
    public static String replaceKeys(String in, Pattern p) {
        return replaceKeys(in, p, false, 1, System.getProperties(), System.getenv());
    }
    
    public static synchronized String readStream(InputStream is) {
		Scanner s = new Scanner(is);
		Throwable arg1 = null;

		String arg2;
		try {
			arg2 = s.useDelimiter("\\A").next();
		} catch (Throwable arg11) {
			arg1 = arg11;
			throw arg11;
		} finally {
			if (s != null) {
				if (arg1 != null) {
					try {
						s.close();
					} catch (Throwable arg10) {
						arg1.addSuppressed(arg10);
					}
				} else {
					s.close();
				}
			}

		}

		return arg2;
	}

    public static synchronized void writeFile(File f, String s) {
		try {
			PrintWriter ex = new PrintWriter(f);
			Throwable arg2 = null;

			try {
				ex.write(s);
			} catch (Throwable arg12) {
				arg2 = arg12;
				throw arg12;
			} finally {
				if (ex != null) {
					if (arg2 != null) {
						try {
							ex.close();
						} catch (Throwable arg11) {
							arg2.addSuppressed(arg11);
						}
					} else {
						ex.close();
					}
				}

			}
		} catch (Exception arg14) {
			System.out.println("Enter a Error Message");
		}

	}
}
