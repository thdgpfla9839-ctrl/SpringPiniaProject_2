pipeline{
	stages{
		stage('Git Connection Check'){
			steps{
				echo "==================="
				echo "Git 연결 확인"
				echo "==================="
				git branch: 'main',
				    url: 'https://github.com/thdgpfla9839-ctrl/SpringPiniaProject_2.git'
				echo "==================="
				echo "Git 연결 완료"
				echo "==================="
			}
		}
	}
}