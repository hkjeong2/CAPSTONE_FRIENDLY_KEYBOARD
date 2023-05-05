package com.example.friendlykeyboard.keyboard

interface KeyboardInteractionListener {
    //InputMethodService를 상속받아 키보드가 필요한 경우 호출될 수 있도록 하고
    //입력방식에 따라 한글, 영어, 숫자, 특수문자 등의 키보드를 출력
    fun modechange(mode:Int)

    //키보드 클릭 시 textfield의 text send
    fun sendText(text : String)

    //키보드 엔터 시 입력된 text 검사 , 제재 기능 정수로 구분
    fun checkText(text : String)
}