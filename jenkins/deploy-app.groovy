// ed08f3a9e0d646b9b0f6adddb5cb6429
final String url = "34.85.236.100"
final String app_name="magshimimPlus"
final String app_version="0.0.1"
final String host_port="8082"
pipeline {
    agent { docker { image 'docker:latest' } }
    stages {
        stage('build') {
            steps {
                sh "docker build -t  einavl/magshimim-web:${app_version} web-server"
            }
        }
         stage('push') {
            steps {
                withCredentials([string(credentialsId: 'DockerHubPwd', variable: 'dockerpwd')]) {
                sh """
                    docker login -u einavl -p ${dockerpwd} 
                    docker push  einavl/magshimim-web:${app_version}
                """
                }

            }
    }
    stage('redeploy'){
        steps{
            sshagent(credentials : ['ssh-einav-inst']) {
            sh "ssh -o StrictHostKeyChecking=no einav.leybsohor@${url} sudo docker stop ${app_name} | true"
            sh "ssh -o StrictHostKeyChecking=no einav.leybsohor@${url} sudo docker run --pull=always --name=${app_name} --rm -d -p ${host_port}:80  einavl/magshimim-web:${app_version}"
    
        }
        }
    }

    stage('Tests'){
         agent { docker { image 'curlimages/curl' } }
        steps{
            script {
                    final String response = sh(script: "curl -s $url", returnStdout: true).trim()
                    echo response
                }
        }
    }
    }
}