radicle-migration-tool
=====================

![Build](https://github.com/cytechmobile/radicle-migration-tool/workflows/build/badge.svg)

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#features">Features</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#command-line-interface">Command-line interface</a></li>
        <li><a href="#requirements">Requirements</a></li>
        <li><a href="#important-notes">Important Notes</a></li>
        <li><a href="#environment-variables">Environment Variables</a></li>
      </ul>
    </li>
    <li>
      <a href="#binaries">Binaries</a>
      <ul>
        <li><a href="#jar-binary">JAR Binary</a></li>
        <li><a href="#native-binaries">Native Binaries</a></li>
        <li><a href="#docker-image">Docker Image</a></li>
        <li><a href="#pre-built-binaries">Pre-built binaries</a></li>
      </ul>
    </li>
    <li><a href="#building-from-source">Building from source</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

## About The Project
This Command Line Interface (CLI) tool enables you to seamlessly migrate your project from GitHub and GitLab to Radicle.

To utilize this tool, you have a few options:
* You can download one of the pre-built binaries from the project's GitHub [releases](https://github.com/cytechmobile/radicle-migration-tool/releases). 
* Alternatively, you can use the provided docker image: `docker pull ghcr.io/cytechmobile/radicle-migration-tool:latest` 
* If you prefer, you can also build a binary directly from the source code.

It is recommended to have the latest version of Radicle CLI installed (`rad`) and the HTTP daemon up and running (`rad web`). Installation instructions for `rad` are available [here](https://radicle.xyz/guides/user#installation).

This tool is available under [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

**Important Note:** Please note that since Radicle is still under active development, our functionality may require adjustments in the future to ensure compatibility.

### Features
The tool offers a range of essential features to facilitate migration, including:

#### GitHub Migration
For migrating issues from GitHub projects, some important features include:
* It enables the migration of all GitHub issues from the source repository in a single run.
* It migrates essential information such as the `Title`, `Description`, `Status`, `Labels`, `Comments`, `Events`, and `Milestone` details.
* It supports the migration of inline assets/files discovered within GitHub issues and comments as Radicle embeds. A GitHub `user_session` cookie must be provided when migrating assets/files from a private GitHub repository by using either the `--github-session` CLI parameter or the `GH_SESSION` environment variable. Login to GitHub via your browser and copy the value of the `user_session` cookie. 
* Any additional information that doesn't fit within the Issue model is preserved in a dedicated `GitHub Metadata` section, along with references to the original repository
* It supports incremental migration, allowing you to rerun the tool (e.g., on a schedule) and create only the newest issues that haven't been previously migrated.
* It offers a range of filtering options to streamline the issue migration process, including issues created after a specified time, issues with specific labels, issues in a particular state, issues belonging to a given milestone number, issues created by a specific user, and issues assigned to a particular user.
* It is available in different binary forms, providing flexibility in how it can be utilized.

Additionally, the tool provides a convenient way to migrate your GitHub Wiki to your Radicle project under the `.wiki` directory, offering a quick solution until Radicle potentially introduces support for Wikis. Notable features include:
* Preservation of the Wiki's repository commit history through the use of the `git subtree` command.
* Facilitation of ongoing migration for any updates in the Wiki's repository via the `git subtree pull` command. 

#### GitLab Migration
For migrating issues from GitLab projects, some important features include:
* It enables the migration of all GitLab issues from the source repository in a single run.
* It migrates essential information such as the `Title`, `Description`, `Status`, `Labels`, `Comments`, `Events`, and `Milestone` details.
* It supports the migration of inline assets/files discovered within GitLab issues and comments as Radicle embeds. A GitLab `_gitlab_session` cookie must be provided when migrating assets/files from a private GitLab repository by using either the `--gitlab-session` CLI parameter or the `GL_SESSION` environment variable. Login to GitLab via your browser and copy the value of the `_gitlab_session` cookie.
* Any additional information that doesn't fit within the Issue model is preserved in a dedicated `GitLab Metadata` section, along with references to the original repository
* It supports incremental migration, allowing you to rerun the tool (e.g., on a schedule) and create only the newest issues that haven't been previously migrated.
* It offers a range of filtering options to streamline the issue migration process, including issues created after a specified time, issues with specific labels, issues in a particular state, issues belonging to a given milestone number, issues created by a specific user, and issues assigned to a particular user.

## Usage

### Command-line interface
#### GitHub Migration
To migrate issues from a GitHub repository, execute the issues subcommand as follows:

```bash 
Usage: radicle-migration-tool github issues [-gv=<gVersion>] [-gu=<gUrl>] -gr=<gRepo> -go=<gOwner> -gt [-gs=<gSession>] [-rv=<rVersion>] [-ru=<rUrl>] -rp=<rProject> -rh [-fs=<fSince>] [-fl=<fLabels>] [-ft=<fState>] [-fm=<fMilestone>] [-fa=<fAssignee>] [-fc=<fCreator>] [-dr]

Migrate issues from a GitHub repository to a Radicle project.       
   
      -gv, --github-api-version=<gVersion>      The version of the GitHub REST API (default: 2022-11-28).
      -gu, --github-api-url=<gUrl>              The base url of the GitHub REST API (default: https://api.github.com).
      -gr, --github-repo=<gRepo>                The source GitHub repo.
      -go, --github-repo-owner=<gOwner>         The owner of the source GitHub repo.
      -gt, --github-token                       Your GitHub personal access token (with repo scope or read-only access granted).
      -gd, --github-domain=<gDomain>            The GitHub domain. It is utilized for migrating assets and files (default: github.com).
      -gs, --github-session                     The value of the user_session cookie. It is utilized for migrating assets and files from a private GitHub repository.
      -rv, --radicle-api-version=<rVersion>     The version of the Radicle HTTP API (default: v1).
      -ru, --radicle-api-url=<rUrl>             The base url of Radicle HTTP API (default: http://localhost:8080/api).
      -rp, --radicle-project=<rProject>         The target Radicle project.
      -rh, --radicle-passphrase=<rPassphrase>   Your radicle passphrase.
      -fs, --filter-since=<fSince>              Migrate issues created after the given time (default: lastRun in store.properties file, example: 2023-01-01T10:15:30+01:00).
      -fl, --filter-labels=<fLabels>            Migrate issues with the given labels given in a csv format (example: bug,ui,@high).
      -ft, --filter-state=<fState>              Migrate issues in this state (default: all, can be one of: open, closed, all).
      -fm, --filter-milestone=<fMilestone>      Migrate issues belonging to the given milestone number (example: 3).
      -fa, --filter-assignee=<fAssignee>        Migrate issues assigned to the given user name.
      -fc, --filter-creator=<fCreator>          Migrate issues created by the given user name.
      -dr, --dry-run                            Run the whole migration process without actually creating the issues in the target Radicle project.
```

To migrate a wiki, execute the `wiki` subcommand as follows:
```bash 
Usage: radicle-migration-tool github wiki -gr=<gRepo> -go=<gOwner> -gt -rpp=<rProjectPath>

Migrate a GitHub Wiki to a Radicle project.

      -gr, --github-repo=<gRepo>                  The source GitHub repo.
      -go, --github-repo-owner=<gOwner>           The owner of the source GitHub repo.
      -gt, --github-token                         Your GitHub personal access token (with `repo` scope or `read-only access` granted).
      -rpp, --radicle-project-path=<rProjectPath> The absolute path to the target Radicle project in your local file system.
```

#### GitLab Migration
To migrate issues from a GitLab project, execute the issues subcommand as follows:

```bash
Usage: radicle-migration-tool gitlab issues [-gv=<gVersion>] [-gu=<gUrl>] -gp=<gProject> -gn=<gNamespace> -gt [-gd=<gDomain>] [-gs=<gSession>] [-rv=<rVersion>] [-ru=<rUrl>] -rp=<rProject> -rh [-fs=<fSince>] [-fl=<fLabels>] [-ft=<fState>] [-fm=<fMilestone>] [-fa=<fAssignee>] [-fc=<fCreator>] [-dr]

Migrate issues from a GitLab repository to a Radicle project.

      -gv, --gitlab-api-version=<gVersion>  The version of the GitLab REST API (default: v4).
      -gu, --gitlab-api-url=<gUrl>          The base url of the GitLab REST API (default: https://gitlab.com/api).
      -gp, --gitlab-project=<gProject>      The source GitLab project.
      -gn, --gitlab-namespace=<gNamespace>  The namespace of the source GitLab project.
      -gt, --gitlab-token                   Your GitLab personal access token.
      -gd, --gitlab-domain=<gDomain>        The GitLab domain. It is utilized for migrating assets and files (default: gitlab.com).
      -gs, --gitlab-session=<gSession>      The value of the _gitlab_session cookie. It is utilized for migrating assets and files from a GitLab project with the `Require authentication to view media files` enabled.
      -rv, --radicle-api-version=<rVersion> The version of the Radicle HTTP API (default: v1).
      -ru, --radicle-api-url=<rUrl>         The base url of Radicle HTTP API (default: http://localhost:8080/api).
      -rp, --radicle-project=<rProject>     The target Radicle project.
      -rh, --radicle-passphrase             Your radicle passphrase.
      -fs, --filter-since=<fSince>          Migrate issues created after the given time (default: timestamp of the last run, example: 2023-01-01T10:15:30+01:00).
      -fl, --filter-labels=<fLabels>        Migrate issues with the given labels given in a csv format (example: bug,ui,@high).
      -ft, --filter-state=<fState>          Migrate issues in this state (default: all, can be one of: open, closed, all).
      -fm, --filter-milestone=<fMilestone>  Migrate issues belonging to the given milestone (example: 'Milestone 1')
      -fa, --filter-assignee=<fAssignee>    Migrate issues assigned to the given user name.
      -fc, --filter-creator=<fCreator>      Migrate issues created by the given user name.
      -dr, --dry-run                        Run the whole migration process without actually creating the issues in the target Radicle project.
```

### Requirements
To use this application, you'll need to fulfill some common requirements, as well as specific requirements based on the binary you choose:
* A GitHub account with a Personal Access Token (PAT). Your PAT must grant you with `repo` scope - in case of a classic one - or with `read-only access` - in case of a fine-grained one. 
* The `rad` Command Line Interface (CLI) tool installed, preferably the latest version. You can find installation details [here](https://radicle.xyz/guides/user#installation). To check the version, run `rad --version`.
* A running instance of the Radicle HTTP daemon. Before starting the daemon, ensure that you have set the `RAD_PASSPHRASE` environment variable or executed the `rad auth` command in the same terminal. Refer to [this link](https://radicle.xyz/guides/user#come-into-being-from-the-elliptic-aether) for examples on how to use the `rad auth` command. To start the Radicle HTTP daemon, run `rad web`.
* A Radicle-initialized Git repository. This will serve as your target Radicle project, where the issues will be migrated.
* If running the JAR binary, ensure that you have Java 17 or a later version installed on your machine.
* If using the Docker image, make sure that the Docker daemon is up and running on your machine.

### Important Notes 
Since the Issue model and HTTP API in Radicle are currently simpler compared to GitHub, we have implemented the following alternative solutions / adaptations:
* We have included a special `GitHub Metadata` / `GitLab Metadata` section at the beginning of the `Description` and each `Comment` of the Radicle Issue. This section contains extra information and links to the original GitHub/GitLab Issue, presented in a table format with columns such as `Issue Number`, `Created On`, `Created By`, `Assignees`, `Milestone`, and `Due By`.
* GitHub/GitLab `Events` are migrated as Radicle `Comments`.
* GitHub/GitLab `Milestones` are migrated as Radicle `Labels`. Additional information can be found in the `GitHub Metadata` / `GitLab Metadata` section in the `Description` of each Radicle `Issue` (if there is any).
* The user who runs the tool will be listed as the `Creator` of all Radicle issues.
* Any links to images within the `Description` and `Comments` of GitHub Issues will still point to GitHub's servers. This means that images will display correctly for public GitHub repositories. However, for private repositories, since authorization is required, you will need to copy the source URL and access it through your browser.

Furthermore, it's important to note that as of now, Radicle does not offer support for Wikis. Consequently, this tool serves as a helpful means to swiftly migrate your GitHub Wiki to a `.wiki` directory within your Radicle project, while also transferring the commit history from the source git repository by utilizing the `git subtree` command. 

### Environment Variables
You can pass any of the command line options via environment variables. Here is the complete list of the supported environment variables:

#### Radicle
* RAD_API_VERSION: The version of the Radicle HTTP API (default v1)
* RAD_API_URL: The base url of Radicle HTTP API (default http://localhost:8080/api)
* RAD_PROJECT: The target Radicle project
* RAD_PROJECT_PATH: The path of the target Radicle project in your local file system
* RAD_PASSPHRASE: Your radicle passphrase

#### GitHub
* GH_API_VERSION: The version of the GitHub REST API (default 2022-11-28)
* GH_API_URL: The base url of the GitHub REST API (default https://api.github.com)
* GH_REPO: The source GitHub repo
* GH_OWNER: The owner of the source GitHub repo
* GH_TOKEN: Your GitHub personal access token (with `repo` scope or `read-only access` granted)
* GH_DOMAIN: The GitHub domain. It is utilized for migrating assets and files (default: github.com).
* GH_SESSION: The value of the `user_session` cookie. It is utilized for migrating assets and files from a private GitHub repository

#### GitLab
* GL_API_VERSION: The version of the GitLab REST API (default: v4)
* GL_API_URL: The base url of the GitLab REST API (default: https://gitlab.com/api)
* GL_PROJECT: The source GitLab project
* GL_NAMESPACE: The namespace of the source GitLab project
* GL_TOKEN: Your GitLab personal access token
* GL_DOMAIN: The GitLab domain. It is utilized for migrating assets and files (default: gitlab.com).
* GL_SESSION: The value of the `_gitlab_session` cookie. It is utilized for migrating assets and files from a GitLab project with the `Require authentication to view media files` enabled

#### Filtering
* FILTER_SINCE: Migrate issues created after the given time (default: lastRun in store.properties file, example: 2023-01-01T10:15:30+01:00).
* FILTER_LABELS: Migrate issues with the given labels given in a csv format (example: bug,ui,@high).
* FILTER_STATE: Migrate issues in this state (default: all, can be one of: open, closed, all).
* FILTER_MILESTONE: Migrate issues belonging to the given milestone number (example: 3).
* FILTER_ASSIGNEE: Migrate issues assigned to the given username.
* FILTER_CREATOR: Migrate issues created by the given username.

#### Other
* LOG_LEVEL: The log level of the application (default INFO)
* STORAGE_FILE_PATH: The path of the storage properties files (default store.properties)
* DRY_RUN: Run the whole migration process without actually creating the issues in the target Radicle project.

For example, to run the command in DEBUG mode, you can execute the following command:

```shell
LOG_LEVEL=DEBUG java -jar radicle-migration-tool-0.5.0.jar issues
```

## Binaries

### JAR Binary
If you intend to use a specific version of the JAR binary (e.g., 0.5.0), execute the following command:
```bash
java -jar radicle-migration-tool-0.5.0.jar issues
```

### Native Binaries
If you intend to use one of the native builds, you need to execute the corresponding native binary. For example, if you have downloaded the binary for Ubuntu, you should execute it by running the following command:
```bash 
./radicle-migration-tool-0.5.0-ubuntu-latest issues
```
### Docker Image
If you intend to use the Docker image, please follow the instructions provided below:

```shell
# Pull the docker image in your local docker registry
docker pull ghcr.io/cytechmobile/radicle-migration-tool:latest

# Tag the docker image in your local docker registry
docker tag ghcr.io/cytechmobile/radicle-migration-tool:latest radicle-migration-tool

# Run the migration
docker run -it -v .:/root/config -v ~/.radicle:/root/.radicle -e RAD_PASSPHRASE=<YOUR_PASSPHRASE> radicle-migration-tool issues
```
To ensure that the `docker run` command executes successfully, the following volumes are required:
* `.:/root/config`: This allows the tool to write a `store.properties` file in your current directory, which helps maintain its state across subsequent runs. IMPORTANT: Please ensure that the folder from which you run the tool has the appropriate write permissions.
* `~/.radicle:/root/.radicle`: This enables the tool to access your Radicle path. If the `rad path` command returns a different path, please update the volume accordingly.

The image assumes that your local Radicle HTTP daemon (`rad web`) is accessible by default at `http://172.17.0.1:8080/api`, where `172.17.0.1` represents the IP address of the host from inside the Docker container. If you need to change this default configuration, you can utilize the available environment variables or CLI options provided.

Lastly, you have the option to pass any environment variable using the -e option of the docker run command. For example: `docker run -e LOG_LEVEL=DEBUG`.

### Pre-built binaries
Pre-built binaries can be downloaded from the project's GitHub [releases page](https://github.com/cytechmobile/radicle-migration-tool/releases). Choose the appropriate release for your operating system and download the associated JAR or executable file.


## Building from source
To build the binary from source code, follow these steps:
1.  Clone the repository from GitHub:
```shell
$ git clone https://github.com/cytechmobile/radicle-migration-tool.git
```

2. Change into the project directory:

```shell
$ cd radicle-migration-tool
```

3. Build the binaries using Maven:

To build JAR binary in Unix run:
```shell
$ ./mvnw package
```

To build a native binary in Unix run:
```shell
$ ./mvnw package -Pnative
```

This will generate the binary file in the `target` directory.

To build a docker image in Unix run:
```shell
$ ./mvnw package -Pdocker
```

## License

Distributed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See `LICENSE` for more information.
