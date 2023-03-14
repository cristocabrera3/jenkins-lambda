pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        AWS_ACCESS_KEY_ID = 'AKIA2HIGE43Y5BCJCNUP'
        AWS_SECRET_ACCESS_KEY = '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'
        GITHUB_REPO_URL = 'https://github.com/cristocabrera3/jenkins-lambda.git'
        STACK_NAME = 'my-stack'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/cristocabrera3/jenkins-lambda.git'
            }
        }

        stage('Deploy') {
            steps {
                bat '"C:\\Program Files\\Amazon\\AWSCLIV2\\aws" cloudformation deploy --region %AWS_REGION% --template-file template.yaml --stack-name %STACK_NAME% --capabilities CAPABILITY_NAMED_IAM'
            }
        }
    }
}