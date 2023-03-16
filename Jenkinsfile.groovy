pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        GITHUB_REPO_URL = 'https://github.com/cristocabrera3/jenkins-lambda.git'
        STACK_NAME = 'my-stack'
        BUCKET_NAME = 'myuniquebucket16032025'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: env.GITHUB_REPO_URL
            }
        }
        stage('Create Bucket') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'cloud_user']]) {
                    bat '"C:\\Program Files\\Amazon\\AWSCLIV2\\aws" s3api create-bucket --bucket %BUCKET_NAME% --region %AWS_REGION%'
                }
            }
        }
        stage('Upload to S3') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'cloud_user']]) {
                    bat '"C:\\Program Files\\Amazon\\AWSCLIV2\\aws" s3 cp lambda_function.py s3://%BUCKET_NAME%/'
                }
            }
        }

        // stage('Build') {
        //     steps {
        //         bat '"C:\\Program Files\\Git\\bin\\bash.exe" -c "mkdir python"'
        //         bat '"C:\\Program Files\\Git\\bin\\bash.exe" -c "mv lambda_function.py python/lambda_function.py"'
        //         zip zipFile: 'python.zip', archive: false, dir: 'python'
        //         archiveArtifacts artifacts: 'python.zip', fingerprint: true
        //     }
        // }
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