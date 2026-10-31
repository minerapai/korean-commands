# Korean Commands (한글 명령어)

다른 모드의 명령어를 한글로도 쓸 수 있게 해 주는 모드입니다.

- 버전: 1.0.0
- Minecraft 1.21.1 / Fabric
- 필요한 모드: Fabric API
- 설치: 서버에만 넣으면 됩니다. 접속하는 사람(클라이언트)은 이 모드가 없어도 됩니다.

## 기능

- 기본 한글 명령어: /업적 = /achv, /퀘스트 = /quest, /라이벌 = /quest rival, /타워 = /tower, /타워상점 = /tower shop, /타워순위 = /tower top, /집 = /home, /집설정 = /sethome, /집삭제 = /delhome, /집목록 = /homes, /알기술 = /tmegg, /이벤트 = /pokeevent, /포켓몬아이템 = /pokeitem
- 원래 명령어 뒤에 붙는 값도 그대로 쓸 수 있습니다 (예: /집 마을).

## 사용법

| 명령어 | 설명 |
|---|---|
| `/<한글 명령어>` | 연결된 원래 명령어가 실행됩니다. |

(OP) 표시가 있는 명령어는 서버 관리자(OP)만 쓸 수 있습니다.

## 설정

`config/korean-commands.json` 에서 한글 명령어를 추가하거나 바꿀 수 있습니다. ("한글": "원래 명령어" 형식)

## 빌드

```bash
./gradlew build
```

`build/libs/` 에 만들어진 jar 파일을 서버의 `mods` 폴더에 넣습니다.

## 라이선스

MIT License. 출처(minerapai)만 밝히면 누구나 자유롭게 쓰고 고치고 배포할 수 있습니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 보세요.
