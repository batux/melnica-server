package melnica.server.host.context.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import melnica.server.host.Host;
import melnica.server.host.context.deployer.WebAppDeployer;
import melnica.server.host.context.deployer.decider.DeployerDecider;
import melnica.server.host.context.deployer.model.DeployerContext;
import melnica.server.host.context.deployer.task.DeployerTask;

public class WebAppManager {

	private static final String WEB_APP_PATH = "webapps";
	
	private File webAppBaseFile;
	private Host host;
	private ExecutorService executor;
	private DeployerDecider decider;
	
	public WebAppManager(Host host) {
		this.host = host;
		this.executor = Executors.newFixedThreadPool(10);
		this.decider = new DeployerDecider();
		this.webAppBaseFile = new File(System.getProperty("user.dir") + File.separator + WEB_APP_PATH);
	}
	
	public void process() {
		
		if(!this.getWebAppBaseFile().exists()) {
			throw new RuntimeException(WEB_APP_PATH + " folder not found in server.");
		}
		
		List<Future<?>> submitResults = new ArrayList<Future<?>>();
		String[] webApps = this.webAppBaseFile.list();
		String webAppBaseFilePath = this.webAppBaseFile.getAbsolutePath();
		for(String webApp : webApps) {
			
			File appFile = new File(this.getWebAppBaseFile(), webApp);
			WebAppDeployer deployer = this.decider.decide(appFile);
			if(deployer == null) {
				continue;
			}
			
			submitResults.add( 
					this.executor.submit(new DeployerTask(this.host, deployer, new DeployerContext(appFile, webApp, webAppBaseFilePath))) );
		}
		
		for (Future<?> submitResult : submitResults) {
            try {
            	submitResult.get();
            } 
            catch (Exception e) {
            	throw new RuntimeException(e);
            }
        }
		this.executor.shutdown();
	}
	
	public File getWebAppBaseFile() {
		return this.webAppBaseFile;
	}
}
