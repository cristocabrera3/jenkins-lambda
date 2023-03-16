pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        GITHUB_REPO_URL = 'https://github.com/cristocabrera3/jenkins-lambda.git'
        STACK_NAME = 'my-stack'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: env.GITHUB_REPO_URL
            }
        }
        stage('Build') {
            steps {
                sh 'mkdir archive'
                sh 'echo test > archive/test.txt'
                zip zipFile: 'test.zip', archive: false, dir: 'archive'
                archiveArtifacts artifacts: 'test.zip', fingerprint: true
            }
        }
        stage('Deploy') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'cloud_user']]) {
                    bat '"C:\\Program Files\\Amazon\\AWSCLIV2\\aws" cloudformation deploy --region %AWS_REGION% --template-file template.yaml --stack-name %STACK_NAME%'
                }
            }
        }
        

        // stage('Test') {
        //     steps {
        //         script {
        //             def validateTemplate = sh(script: '"C:\\Program Files\\Amazon\\AWSCLIV2\\aws" cloudformation validate-template --template-body file://template.yaml --region %AWS_REGION%', returnStatus: true)

        //             if (validateTemplate == 0) {
        //                 echo "CloudFormation template is valid"
        //             } else {
        //                 error "CloudFormation template is invalid"
        //             }
        //         }
        //     }
        // }
    }
}