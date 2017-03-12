package net.wheatlauncher;

import api.launcher.ARML;
import net.launcher.FXEventBus;
import net.launcher.LaunchCore;
import net.launcher.utils.NIOUtils;
import net.wheatlauncher.control.utils.FinalFieldSetter;
import org.to2mbn.jmccc.util.Platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


/**
 * @author ci010
 */
public class CoreTest
{
	private LaunchCore core;
	private Path root;

	public static void main(String[] args) throws Exception
	{
		CoreTest coreTest = new CoreTest();
		coreTest.preinit();
		coreTest.core.init(coreTest.root);
		while (true)
		{
			Thread.sleep(100000);
		}
	}

	private Logger createLogger() throws IOException
	{
		Logger logger = Logger.getLogger("ARML");
		Files.createDirectories(root.resolve("logs"));
		FileHandler logs = new FileHandler(root.resolve("logs").resolve("main.log").toAbsolutePath().toString());
		Formatter formatter = new Formatter()
		{
			String simpleFormat = "[%1$tl:%1$tM:%1$tS %1$Tp] [%2$s] [%4$s]: %5$s%6$s%n";
			private final Date dat = new Date();

			@Override
			public synchronized String format(LogRecord record)
			{
				dat.setTime(record.getMillis());
				String source;
				if (record.getSourceClassName() != null)
				{
					source = record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf('.') + 1);
					if (record.getSourceMethodName() != null)
						source += "::" + record.getSourceMethodName();
				}
				else
					source = record.getLoggerName();
				String message = formatMessage(record);
				String throwable = "";
				if (record.getThrown() != null)
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					pw.println();
					record.getThrown().printStackTrace(pw);
					pw.close();
					throwable = sw.toString();
				}
				return String.format(simpleFormat,
						dat,
						source,
						record.getLoggerName(),
						record.getLevel().getLocalizedName(),
						message,
						throwable);
			}
		};
		logs.setFormatter(formatter);
		logger.addHandler(logs);

		return logger;
	}

	private static Path loadLocation() throws IOException
	{
		Path root;
		switch (Platform.CURRENT)
		{
			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				root = Paths.get(appdata == null ? System.getProperty("user.home", ".") : appdata);
				break;
			case LINUX:
				root = Paths.get(System.getProperty("user.home", "."));
				break;
			case OSX:
				root = Paths.get("Library/Application Support/");
				break;
			default:
				root = Paths.get(System.getProperty("user.home", ".") + "/");
		}
		Path redirect = root.resolve("arml.loc");
		if (Files.exists(redirect))
		{
			String s = NIOUtils.readToString(redirect);
			try {return Paths.get(s);}
			catch (Exception e) {return root.resolve("arml");}
		}
		return root.resolve(".launcher");
	}

	private void preinit() throws Exception
	{
		root = loadLocation();
		if (!Files.exists(root)) Files.createDirectories(root);
		ARML instance = ARML.instance();
		core = new Core();
		for (Field field : instance.getClass().getDeclaredFields())
		{
			if (field.getName().equals("bus"))
				FinalFieldSetter.INSTANCE.set(instance, field, new FXEventBus());
			else if (field.getName().equals("context"))
				FinalFieldSetter.INSTANCE.set(instance, field, core);
			else if (field.getName().equals("logger"))
				FinalFieldSetter.INSTANCE.set(instance, field, createLogger());
			else if (field.getName().equals("scheduledExecutorService"))
				FinalFieldSetter.INSTANCE.set(instance, field, Executors.newScheduledThreadPool(4));
			else if (field.getName().equals("taskCenter"))
				FinalFieldSetter.INSTANCE.set(instance, field, core);
		}
	}
}
