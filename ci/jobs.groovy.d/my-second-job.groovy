pipeline {
    agent any
    stages {
        stage('Setup') {
            steps {
                echo 'Setting up...'
            }
        }
        stage('Execution') {
            steps {
                echo 'Executing...'
            }
        }
        stage('Completion') {
            steps {
                echo 'Finishing...'
            }
        }
    }
}
