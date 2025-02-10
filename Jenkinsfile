pipeline {
    agent any

    stages {
        stage('Copy Environment Variable File') {
            steps {
	              script {
                  // withCredentials : Credentials 서비스를 활용하겠다
                  // file : secret file을 불러오겠다
                  // credentialsId : 불러올 file의 식별 ID
                  // variable : 블록 내부에서 사용할 변수명
                  withCredentials([file(credentialsId: 'env-file', variable: 'env_file')]) {
                    // Jenkins 서비스 내 .env 파일을 파이프라인 프로젝트 내부로 복사
                    sh 'cp $env_file .env'

                    // 파일 권한 설정
                    // 소유자 : 읽기 + 쓰기
                    // 그 외 : 읽기 권한
                    // 권한) 읽기(4) 쓰기(2) 실행(1)
                    sh 'chmod 644 .env'
                  }
                }
            } 
        }

        stage('Docker Image Build & Container Run') {
          steps {
            script {
              sh 'docker compose build'

              sh 'docker compose down'

              sh 'docker compose up -d'
            }
          }
        }

        stage('Build Start') {
          steps {
            script {
              // Jenkins Credentials에서 Secret Text 가져오기
              // credentialsId : credentials 생성 당시 작성한 
              // variable : 스크립트 내부에서 사용할 변수 이름 
              withCredentials([string(credentialsId: 'discord-webhook', variable: 'discord_webhook')]) {
                discordSend description: """
                Jenkins Build Start
                """,
                link: env.BUILD_URL, 
                title: "${env.JOB_NAME} : ${currentBuild.displayName} 시작", 
                webhookURL: "$discord_webhook"
              }
            }
          }
        }
    
    }
    
    post {
        success {
            withCredentials([string(credentialsId: 'discord-webhook', variable: 'discord_webhook')]) {
                        discordSend description: """
                        제목 : ${currentBuild.displayName}
                        결과 : ${currentBuild.currentResult}
                        실행 시간 : ${currentBuild.duration / 1000}s
                        """,
                        link: env.BUILD_URL, result: currentBuild.currentResult, 
                        title: "${env.JOB_NAME} : ${currentBuild.displayName} 성공", 
                        webhookURL: "$discord_webhook"
            }
        }
        failure {
            withCredentials([string(credentialsId: 'discord-webhook', variable: 'discord_webhook')]) {
                        discordSend description: """
                        제목 : ${currentBuild.displayName}
                        결과 : ${currentBuild.currentResult}
                        실행 시간 : ${currentBuild.duration / 1000}s
                        """,
                        link: env.BUILD_URL, result: currentBuild.currentResult, 
                        title: "${env.JOB_NAME} : ${currentBuild.displayName} 실패", 
                        webhookURL: "$discord_webhook"
            }
        }
    }
}