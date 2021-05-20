package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class Backend implements AutoCloseable {
	private final ConfigurableApplicationContext appCtx;

	public Backend() throws Exception {
		this.appCtx = new SpringApplication(App.class).run();
	}

	@Override
	public void close() throws Exception {
		appCtx.close();
	}

	public static void main(String[] args) throws Exception {
		try (Backend m = new Backend()) {
			// loop forever
			for (;;) {
				Thread.sleep(60000);
			}
		}
	}
}
