import jenkins.model.*
import hudson.model.*
import org.jenkinsci.plugins.workflow.job.* // For pipeline job type
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

println "--> Creating Jenkins jobs from Groovy files"

// Define the directory containing job Groovy scripts
def jobsDirectory = new File('/var/jenkins_home/jobs.groovy.d')

// Check if the directory exists and contains files
if (jobsDirectory.exists() && jobsDirectory.isDirectory()) {
    jobsDirectory.eachFile { file ->
        if (file.name.endsWith(".groovy")) {
            println "--> Processing job file: ${file.name}"
            def jobName = file.name.split('\\.')[0] // Use file name (without extension) as job name
            def pipelineScript = file.text

            // Check if the job already exists
            def existingJob = Jenkins.instance.getItem(jobName)
            if (existingJob) {
                println "--> Job '${jobName}' already exists, skipping creation"
            } else {
                println "--> Creating new job: ${jobName}"

                // Create a new pipeline job
                WorkflowJob job = Jenkins.instance.createProject(WorkflowJob, jobName)

                // Define the pipeline using the content of the Groovy file
                def flowDefinition = new CpsFlowDefinition(pipelineScript, true) // true for sandbox execution

                job.setDefinition(flowDefinition)
                job.save()
                println "--> Job '${jobName}' created successfully"
            }
        }
    }
} else {
    println "--> No jobs found to create"
}

Jenkins.instance.save()
