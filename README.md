radicle-github-migrate
=====================

![Build](https://github.com/cytechmobile/radicle-github-migrate/workflows/build/badge.svg)

This Command Line Interface (CLI) tool allows to migrate the issues from GitHub repository to your Radicle project.

To use this tool, you can either download one of the pre-built binaries from the project's GitHub [releases](https://github.com/cytechmobile/radicle-github-migrate/releases), or you can build a binary from the source code.

The target rad environment must have a version 0.8.0 rad Command Line Interface (CLI) tool installed and the HTTP daemon (radicle-httpd) up and running. Installation instructions for `rad` are available [here](https://github.com/radicle-dev/heartwood).

This tool is available under [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

### Command-line interface
```bash 
Usage: java -jar radicle-github-migrate-0.1.0.jar issues -gt -go=<gOwner> -gr=<gRepo> [-gu=<gUrl>] [-gv=<gVersion>] -rp=<rProject> [-ru=<rUrl>] [-rv=<rVersion>] 

Migrate GitHub issues       
   -go, --github-repo-owner=<gOwner>     The owner of the target GitHub repo       
   -gr, --github-repo=<gRepo>            The source GitHub repo       
   -gt, --github-token                   Your GitHub personal access token       
   -gu, --github-api-url=<gUrl>          The base url of the GitHub REST API (default https://api.github.com)
   -gv, --github-api-version=<gVersion>  The version of the GitHub REST API (default 2022-11-28) 
   -rp, --radicle-project=<rProject>     The target Radicle project 
   -ru, --radicle-api-url=<rUrl>         The base url of Radicle HTTP API (default http://localhost:8080/api)
   -rv, --radicle-api-version=<rVersion> The version of the Radicle HTTP API (default v1)
```
If you plan to use one of the native builds, you must execute the corresponding native binary. For instance, if you downloaded the binary for Ubuntu, you should execute it by running the following command:
```bash 
./radicle-github-migrate-0.1.0-ubuntu-latest issues -gt -go=<gOwner> -gr=<gRepo> [-gu=<gUrl>] [-gv=<gVersion>] -rp=<rProject> [-ru=<rUrl>] [-rv=<rVersion>]
```

### Requirements
In order to use this application, you'll need:
* Java 17 or later installed on your machine (to run the pre-built JAR binary)
* A GitHub account with a personal access token
* The `rad` Command Line Interface (CLI) tool installed, at least v0.8.0. Please check [here](https://github.com/radicle-dev/heartwood) for installation details.  To check the version run `rad --version`
* A Radicle-initialised Git repo
* A running instance of the Radicle HTTP deamon. Before you start the deamon, make sure that you have set the `RAD_PASSPHRASE` environment variable or have executed the `rad auth` command on the same terminal. Refer to [this link](https://github.com/radicle-dev/heartwood/blob/master/radicle-cli/examples/rad-auth.md) for examples on how to use the `rad auth` command. To start the Radicle HTTP daemon run `radicle-httpd`.

### Environment Variables
You can pass any of the command line options via environment variables. Here is the complete list of the supported environment variables:
* GITHUB_API_VERSION: The version of the GitHub REST API (default 2022-11-28)
* GITHUB_API_URL: The base url of the GitHub REST API (default https://api.github.com)
* GITHUB_REPO: The source GitHub repo
* GITHUB_OWNER: The owner of the source GitHub repo
* GITHUB_TOKEN: Your GitHub personal access token
* RADICLE_API_VERSION: The version of the Radicle HTTP API (default v1)
* RADICLE_API_URL: The base url of Radicle HTTP API (default http://localhost:8080/api)
* RADICLE_PROJECT: The target Radicle project
* LOG_LEVEL: The log level of the application (default INFO)

For example, to run the command in DEBUG mode, you can execute the following command:

```shell
LOG_LEVEL=DEBUG java -jar radicle-github-migrate-0.1.0.jar issues -gt -go=<gOwner> -gr=<gRepo> [-gu=<gUrl>] [-gv=<gVersion>] -rp=<rProject> [-ru=<rUrl>] [-rv=<rVersion>]
```

### Building from source
To build the binary from source code, follow these steps:
1.  Clone the repository from GitHub:
```shell
$ git clone https://github.com/cytechmobile/radicle-github-migrate.git
````

2. Change into the project directory:

```shell
$ cd radicle-github-migrate
```

3. Build the binaries using Maven:

To build JAR binary in Unix run:
```shell
$ ./mvnw package
```

To build JAR binary in Windows run:
```shell
$ ./mvnw.cmd package
```

To build a native binary in Unix run:
```shell
$ ./mvnw package -Pnative
```

To build a native binary in Windows run:
```shell
$ ./mvnw.cmd package -Pnative
```


This will generate the binary file in the `target` directory.

### Downloading pre-built binaries
Pre-built binaries can be downloaded from the project's GitHub [releases page](https://github.com/cytechmobile/radicle-github-migrate/releases). Choose the appropriate release for your operating system and download the associated JAR or executable file.
