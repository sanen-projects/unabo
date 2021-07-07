package online.sanen.unabo.extend.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Assert;
import com.mhdt.toolkit.PathUtility;

/**
 * 
 * @author lazyToShow <br>
 *         Date: 2020年12月17日 <br>
 *         Time: 下午3:54:07 <br>
 */
public class ResourceLoad {

	/**
	 * 
	 * file: XXX classpath: XX/XX/
	 * 
	 * @param path
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void load(String path) throws IOException, URISyntaxException {

		int lastSeparatorIndex = path.lastIndexOf("/");
		String suffix = path.substring(lastSeparatorIndex + 1);

		/** Sub suffix from path */
		if (suffix.contains("."))
			path = path.substring(0, lastSeparatorIndex);

		if (path.startsWith("file:")) {

			List<File> toLoadFiles = getListFromLocal(new File(path.replace("file:", "")), path, suffix);
			load(toLoadFiles);

		} else if (path.startsWith("classpath:")) {
			path = path.replace("classpath:", "");

			/** Real path */
			URL url = PathUtility.getClassPath(path);

			Assert.notNull(url, "Path doesn't exist: \"classpath:%s\"", path);
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

			/** Current ev is jar */
			if (url.toString().startsWith("jar:")) {
				List<String> toLoadPaths = getListFromJar(url, path, suffix);
				toLoadPaths.forEach(toLoadPath -> {
					InputStream inputStream = contextClassLoader.getResourceAsStream(toLoadPath);
					YAMLMapperConfiguring.load1(toLoadPath, inputStream);
				});

			} else {
				List<File> toLoadFiles = getListFromClassPath(url, path, suffix);
				load(toLoadFiles);
			}

		}

	}

	/**
	 * 
	 * @param toLoadFiles
	 */
	private void load(List<File> toLoadFiles) {

		toLoadFiles.forEach(toLoadFile -> {
			try {
				YAMLMapperConfiguring.load1(toLoadFile.getAbsolutePath(), new FileInputStream(toLoadFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 
	 * @param file
	 * @param path
	 * @param suffix
	 * @return
	 */
	private List<File> getListFromLocal(File file, String path, String suffix) {

		List<File> list = new LinkedList<>();

		for (File tempFile : file.listFiles()) {

			if (tempFile.isDirectory())
				list.addAll(getListFromLocal(tempFile, path, suffix));
			else if (tempFile.isFile() && checkSuffix(tempFile.getName(), suffix))
				list.add(tempFile);
		}

		return list;
	}

	/**
	 * 
	 * @param url
	 * @param path
	 * @param suffix
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	private List<File> getListFromClassPath(URL url, String path, String suffix)
			throws URISyntaxException, MalformedURLException {

		List<File> list = new LinkedList<>();

		File file = new File(url.toURI());

		for (File tempFile : file.listFiles()) {

			if (tempFile.isDirectory())
				list.addAll(getListFromClassPath(tempFile.toURI().toURL(), path, suffix));
			else if (tempFile.isFile() && checkSuffix(tempFile.getName(), suffix))
				list.add(tempFile);
		}

		return list;
	}

	/**
	 * 
	 * @param name
	 * @param suffix
	 * @return
	 */
	private boolean checkSuffix(String name, String suffix) {
		if (Validate.isNullOrEmpty(suffix))
			return true;

		Pattern pattern = Pattern.compile(suffix.replace("*", "") + "$");
		return pattern.matcher(name).find();
	}

	/**
	 * 
	 * @param url
	 * @param prefix
	 * @param suffix
	 * @return
	 * @throws IOException
	 */
	private List<String> getListFromJar(URL url, String prefix, String suffix) throws IOException {

		String path = url.toString().substring("jar:file:".length() + 1, url.toString().lastIndexOf(".jar") + 4);
		List<String> list = new LinkedList<>();
		JarFile localJarFile = new JarFile(new File(path));
		Enumeration<JarEntry> entries = localJarFile.entries();

		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String jarEntryName = jarEntry.getName();
			if (jarEntryName.contains(prefix) && checkSuffix(jarEntryName, suffix)) {
				list.add(jarEntryName.substring(jarEntryName.indexOf(prefix)));
			}

		}

		return list;
	}

}
