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

        stage('Checkout & Build') {
            steps {
                dir('package') {
                    // Clone the GitHub repository containing the lambda function file
                    git url: 'https://github.com/cristocabrera3/jenkins-lambda.git', branch: 'master'
                    // Move the lambda function file to the package directory
                    sh 'cp ../lambda_function.py .'
                    // Create the lambda function package
                    bat 'powershell Compress-Archive -Path ./* -DestinationPath ../lambda_function.zip'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'aws cloudformation deploy --region $AWS_REGION --template-file template.yaml --stack-name $STACK_NAME --capabilities CAPABILITY_NAMED_IAM LambdaCodeS3Bucket=sam-app --parameter-overrides LambdaCodeS3Key=lambda_function.zip'
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