package com.example.friendlykeyboard.keyboard

interface KeyboardInteractionListener {
    //InputMethodService를 상속받아 키보드가 필요한 경우 호출될 수 있도록 하고
    //입력방식에 따라 한글, 영어, 숫자, 특수문자 등의 키보드를 출력
    fun modechange(mode:Int)
}