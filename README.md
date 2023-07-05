# ☀️ Morning Buddy ☀️


</br>
<img src="https://github.com/2023-MadCamp-HJ/project1/assets/80195979/81a13a02-3d23-44f9-b5d5-e04cd3118d71" width="150" height="auto">
</br>
연락처, 갤러리, 알람 기능을 한 번에 !
<b>모닝 버디</b>와 함께라면 일어날 수밖에! 😎
</br>
</br>

## Team : Speaking Potato
[김현수](https://github.com/leejy12) : KAIST 전산학부 19학번 

[박진아](https://github.com/pja9362) : 성균관대학교 컬처앤테크놀로지융합전공/소프트웨어학과 20학번

## Description
<b>1. 연락처</b>
   - 본인 핸드폰의 연락처 리스트
   - 연락처의 검색 기능
   - 연락처 추가,삭제,제거
  
<b>2. 갤러리</b>
   - 본인 핸드폰의 갤러리와 연동
   - 사진 슬라이드 구성

<b>3. 알람</b>
   - 알람을 설정하세요
   - 지정된 시간에 알람이 울림
   - 세 번의 알람 무시 시 연락처의 첫번째 사람에게 메세지 전송
     
     
## Develop Features
Kotlin을 이용했습니다.

<b>1. 연락처</b>
   - 실제 안드로이드 기기의 연락처를 연동했습니다.
   - 수정, 삭제 또한 실제 안드로이드 기기의 연락처를 수정하는 기능입니다.
   - 한글(초성으로도 가능)으로도 연락처 이름 검색이 가능합니다.
   - 번호 검색도 가능합니다.
   - 쉽게 테스트 할 수 있도록 연락처 추가 페이지에 무작위 이름과 번호를 추가 하는 버튼을 만들었습니다.


<b>2. 갤러리 [Picasso 라이브러리 사용]</b>
   - 기기의 갤러리와 연동하여 실제 사진들을 불러옵니다.
   - (아이폰 갤러리처럼) 최신 이미지를 가장 하단에 배치하며 탭 진입 시 스크롤을 조정하여 최신 이미지가 보이도록 합니다.
   - 카메라와 연동하여 사진을 찍을 수 있고, 촬영한 사진을 바로 앱 내 갤러리에서 확인할 수 있습니다.
   - 격자뷰/파노라마뷰 두 버전으로 이미지를 확인할 수 있습니다.
   - 격자뷰에서는, 사진 길게 클릭 시 이미지 삭제가 가능합니다.
   - 격자뷰에서는, 사진 클릭 시 해당 사진을 크게 볼 수 있으며, 터치이벤트를 통한 확대/축소 기능 및 스와이프 기능을 통한 다른 이미지로의 전환 기능을 제공합니다.
   - 파노라마뷰에서는, 가로 스크롤을 통해 이미지를 연속하여 확인할 수 있습니다.


<b>3. 알람</b>
   - 지정한 시간에 알람이 울립니다.
   - 여러 개의 알람을 설정할 수 있으며, 시간 순서대로(00:00 ~ 23:59) 자동 정렬됩니다.
   - 이미 설정한 알람 시각과 동일한 시각에 알람을 생성하려고 하면, '동일한 시간에 알람이 존재합니다' 팝업이 뜹니다.
   - 정해진 시간 내에 해제하지 않으면, 다시 알림 기능이 활성화되어 5분 후에 알람이 다시 울립니다.
   - 만약 3번까지 다시 알림을 해제하지 않으면, 기기의 연락처 및 문자 앱과 연동하여 연락처의 첫번째 사람에게 '모닝콜 좀 해주세요!' 라고 문자를 보냅니다.
  

<img src="https://github.com/2023-MadCamp-HJ/project1/assets/80195979/b63fc757-060e-4e96-8ecf-8c2f7f4343b7" width="160" height="auto">

<img src="https://github.com/2023-MadCamp-HJ/project1/assets/80195979/d38e489d-63f6-4930-a5ed-a6048dee4bd0" width="160" height="auto">
<img src="https://github.com/2023-MadCamp-HJ/project1/assets/80195979/5e172dfe-f679-4467-b09f-ed0404cc7968" width="160" height="auto">


<img src="https://github.com/2023-MadCamp-HJ/project1/assets/80195979/da0a84dd-254d-492b-aa20-877103c25c5a" width="160" height="auto">
<img src="https://github.com/2023-MadCamp-HJ/project1/assets/80195979/70feb79d-4bd7-45ec-8f16-81cdac7605bb" width="160" height="auto">


## How to download APK
[APK 파일 위치](https://github.com/2023-MadCamp-HJ/project1/raw/main/app/release/app-release.apk)

## Questions & Feedbacks
skykhs3@kaist.ac.kr

