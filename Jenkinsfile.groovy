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
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], userRemoteConfigs: [[url: GITHUB_REPO_URL]]])
            }
        }

        stage('Deploy') {
            steps {
                sh "aws cloudformation deploy --region ${AWS_REGION} --template-file template.yaml --stack-name ${STACK_NAME} --capabilities CAPABILITY_NAMED_IAM --parameter-overrides CodeUri=${GITHUB_URL},Handler=lambda_function.lambda_handler"
            }
        }

        stage('Test') {
            steps {
                script {
                    def endpoint = sh(script: 'aws cloudformation describe-stacks --region $AWS_REGION --stack-name $STACK_NAME --query "Stacks[0].Outputs[?OutputKey==\'HelloWorldApi\'].OutputValue" --output text', returnStdout: true).trim()

                    def response = sh(script: "curl -s $endpoint/hello")
                    echo "Response from API Gateway: ${response.trim()}"

                    assert response.trim() == "hello world"
                }
            }
        }
    }
}