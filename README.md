# Git Tweeter 
*Tweet commits of your Git Repo*

--

Git Tweeter monitors a given set of repositories and tweets the branch & commit message to Twitter. This is a Scala implementation using Akka Actors.

### Setup
* Include your Twitter OAuth credentials in `src/main/Secrets/secrets.txt`
* Include the set of repositories you wish to monitor in `src/main/Secrets/repoCatalogs`

### Usage
* From terminal: `$ sbt console`
* From sbt: `> run-main gittweet.GitTweet`
