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
    }
    
    post {
		    always {
				    echo '항상 실행된다.'
		    }
		    
		    success {
				    echo '성공 시 실행된다.'
		    }
		    
		    failure {
				    echo '실패 시 실행된다.'
		    }
    }
}