#!groovy

import jenkins.model.*
import hudson.security.*
import jenkins.install.*
import jenkins.model.Jenkins

def instance = Jenkins.getInstance()

// Set up admin user
def adminUsername = System.getenv("JENKINS_ADMIN_ID")
def adminPassword = System.getenv("JENKINS_ADMIN_PASSWORD")

if (adminUsername && adminPassword) {
    println "--> Creating admin user"

    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount(adminUsername, adminPassword)
    instance.setSecurityRealm(hudsonRealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)
    instance.save()

    println "--> Admin user '${adminUsername}' created"
} else {
    println "--> Skipping admin user creation. Missing environment variables."
}

// Install suggested plugins without restart
println "--> Installing suggested plugins"
def pluginManager = Jenkins.instance.pluginManager
def updateCenter = Jenkins.instance.updateCenter

// List of suggested plugins
def suggestedPlugins = [
        "workflow-aggregator", // Pipeline
        "git",                 // Git plugin
        "github",              // GitHub plugin
        "matrix-auth",         // Matrix Authorization Strategy
        "credentials",         // Credentials Plugin
        "blueocean"            // Blue Ocean Plugin
]

def pluginsToInstall = suggestedPlugins.findAll { !pluginManager.getPlugin(it) }

if (!pluginsToInstall.isEmpty()) {
    println "--> Installing plugins: ${pluginsToInstall.join(', ')}"
    def deploymentList = pluginsToInstall.collect { updateCenter.getPlugin(it)?.deploy() }

    // Wait for all plugins to be deployed before restarting
    deploymentList.each { it?.get() }

    println "--> Plugins installation complete"
} else {
    println "--> All suggested plugins are already installed"
}

// Skip the setup wizard
Jenkins.instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)
instance.save()

// Delaying the restart to avoid continuous loop
println "--> Jenkins setup completed. You may restart manually if needed."
