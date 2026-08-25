package com.sist.web.aop;
/*
 *    minikube 사용 목적 
 *    1) 비용이 안든다 
 *    2) 자신 컴퓨터에서 쿠바네티스 사용이 가능 (테스트)
 *                   -------- 컨테이너 (도커) 
 *                   -------- DockerHub
 *       => 도커 이미지 
 *          docker bulid -t 이미지명 .
 *       => 도커 태그 생성
 *          docker tag 이미지명 허브명/이미지명 
 *       => 도커 허브 연결 
 *          docker login -u 허브명 
 *          password 등록 
 *       => push 
 *          docker push 허브명/이미지명
 *       => pull 
 *          docker pull 허브명/이미지명
 *          ----- deployment.yaml에 등록 
 *    minikube 실행 
 *      1. minikube start
 *      2. kubectl apply -f ~/k8s/deployment.yaml 
 *      3. kubectl get pods 
 *         => running 상태가 아닌 경우 
 *         => kubectl logs pod_name 
 *      4. kubectl get svc (service) 
 *      5. minikube service service_name 
 *      
 *     * 용어 
 *      1. 클러스트 : 쿠바네티스가 관리하는 전체 컴퓨터 
 *                  => 1개만 사용 (minikube)
 *      2. 노드 : 각 컴퓨터에 있는 서버 
 *      3. 파드 : 실행하는 가장 작은 단위 
 *               ------------------ 컨테이너(도커)
 *      4. 크루 : 쿠바네티스에 명령을 내리는 도구 
 *         kubectl
 *         
 *      동작 순서 
 *      컴퓨터 
 *        |
 *      쿠바네티스 클러스트 
 *        |
 *       노드 : 각 컴퓨터 서버 
 *        | 
 *       파드 : 실행이 가능한 파일 모음  
 *        |
 *       컨테이너 : 실행 단위 (앱)
 *      ------------------------- MSA => 싼 값 : docker-compose
 *     명령어 기억 
 *      => minikube start 
 *      => minikube status 
 *      => kubectl get nodes 
 *      => minikube dashboard
 *      => kubectl get pods 
 *      => kubectl get svc 
 *      => minikube service 서비스명 => 바로 실행 
 *      -----------------------------------------
 *      
 *      실제 환경 
 *      
 *      서버 1 (게시판)
 *      서버 2 (상품)  --------- 쿠바네티스
 *      서버 3 (회원...)
 *      
 *      -------------------------------
 *      컴퓨터 
 *        | 
 *        ------- 미니큐버
 *           |
 *           ------ 쿠버네티스
 *      
 */
public class MiniKube {

}