package melnica.server.host.context.deployer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import melnica.server.host.Host;
import melnica.server.host.context.deployer.decider.DeployerDecider;
import melnica.server.host.context.deployer.model.DeployerContext;

public class WebAppManager {
	
	private File webApplicationsFolder;
	private Host host;
	private ExecutorService executor;
	private DeployerDecider decider;
	
	public WebAppManager(Host host) {
		this.host = host;
		this.executor = Executors.newFixedThreadPool(10);
		this.decider = new DeployerDecider();
		this.webApplicationsFolder = new File(generateWebApplicationRootFilePath());
	}
	
	public void deployWebApplications() {
		
		if(!this.getWebApplicationsFolder().exists()) {
			throw new RuntimeException(this.host.getConfiguration().getRootFolderName() + " folder not found in server.");
		}
		
		List<Future<?>> deployerSubmitResults = submitWebApplicationDeploymentTasks();
		
		for (Future<?> submitResult : deployerSubmitResults) {
            try {
            	submitResult.get();
            } 
            catch (Exception e) {
            	System.out.println("Web application deployment exception: " + e.getMessage());
            }
        }
		this.executor.shutdown();
	}
	
	public File getWebApplicationsFolder() {
		return this.webApplicationsFolder;
	}
	
	
	private List<Future<?>> submitWebApplicationDeploymentTasks() {
		
		List<Future<?>> deployerSubmitResults = new ArrayList<Future<?>>();
		String[] webApplicationFilePaths = this.webApplicationsFolder.list();
		String webApplicationsFolderPath = this.webApplicationsFolder.getAbsolutePath();
		
		for(String webApplicationFilePath : webApplicationFilePaths) {
			
			File webApplicationFile = new File(this.getWebApplicationsFolder(), webApplicationFilePath);
			WebAppDeployer deployer = this.decider.decide(webApplicationFile);
			if(deployer == null) {
				continue;
			}
			
			deployerSubmitResults.add( 
					this.executor.submit(
							new DeployerTask(
									this.host, deployer, 
									new DeployerContext(webApplicationFile, webApplicationFilePath, webApplicationsFolderPath))) );
		}
		return deployerSubmitResults;
	}
	
	private String generateWebApplicationRootFilePath() {
		return System.getProperty("user.dir") + File.separator + this.host.getConfiguration().getRootFolderName();
	}
}
