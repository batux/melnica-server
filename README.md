# Melnica Web Server <img src="https://user-images.githubusercontent.com/2838457/121792180-53048600-cbfa-11eb-831b-1079ebf97b39.png" width="180px" height="90px" align="right" />

Melnica Server is a custom basic Servlet Container application which depends on Socket Programming.

## The Description of Project
Melnica is a web server which process servlets. Melnica is a multithreaded server which creates http request, response objects for each client request. 
For each request, socket connection is established that has output/input streams.

- "conf/melnica.xml" is configuration file of Melnica Server.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Melnica shutdown="SD">
	<Service name="localhost_service_8081" activeWebPlatforms="servlet">
		<Bosphorus port="8081" protocol="HTTP/1.1" timeout="45000" />
		<Host domain="localhost" name="first_local" unpackWars="true" appRootFolderName="webapps" />
	</Service>
	<Service name="localhost_service_8082" activeWebPlatforms="servlet">
		<Bosphorus port="8082" protocol="HTTP/1.1" timeout="45000" />
		<Host domain="localhost" name="second_local" unpackWars="true" appRootFolderName="webapps" />
	</Service>
</Melnica>
```

The details will be continued ...


# Melnica Server

HTML content which served from Melnica Server rendered by browser.

![open](https://user-images.githubusercontent.com/2838457/117578458-1921fa80-b0f7-11eb-99ee-b98ad8f6379e.png)


Http header added to client response.

Melnica: Batuhan Düzgün

![open](https://user-images.githubusercontent.com/2838457/117578463-20490880-b0f7-11eb-94ed-a15e2bd24933.png)

## References
- https://l-webx.gitbooks.io/how_tomcat_works/content/index.html
- https://github.com/xianfengxiong/how-tomcat-work/tree/master/book/tomcat-4.1.12-src
- https://medium.com/@nikhilmanikonda/tomcat-who-i-am-and-what-i-do-e91ff72fb2ea
- https://www.net.t-labs.tu-berlin.de/teaching/computer_networking/ap01.htm
