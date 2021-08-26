import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

public class HuntTheWumpus {
    /*
    게임 상의 방 번호를 표현한 배열
    총 20개의 방이 존재하며 눈으로 세기 쉽도록
    요소의 위치와 같은 번호를 값으로 사용
     */
    public static Integer[] rooms = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19
    };

    /*
    각 방에서 이동해갈 수 있는 다른 방들의 목록을 표현한 배열
    각 요소는 현재 방에서 이동할 수 있는 방들의 번호들로 이루어진 배열
    예를 들어, rooms[1]에서 이동할 수 있는 방들의 목록은 links[1]
    1번 방에서 이동할 수 있는 방들의 목록은 0번, 9번, 2번 방
    다만 1번 방에서 0번, 9번, 2번 방으로 이동할 수 있으므로
    0번, 9번, 2번 방에서도 1번 방으로 이동할 수 있어야 함
     */

    public static Integer[][] links = {
            {1, 7, 4}, {0, 9, 2}, {1, 11, 3}, {2, 13, 4}, {3, 0, 5},
            {4, 14, 6}, {5, 16, 7}, {6, 0, 8}, {7, 17, 9}, {8, 1, 10},
            {9, 18, 11}, {10, 2, 12}, {11, 19, 13}, {12, 3, 14}, {13, 5, 15},
            {14, 19, 16}, {15, 6, 17}, {16, 8, 18}, {17, 10, 19}, {18, 12, 15}
    };

    /*
    각 위험요소의 이름을 나타내는 문자열 변수
    반복되어 사용되는 문자열이 존재하는 경우 문자열을 변수로 선언해 사용하여
    문자열을 잘못 입력하는 실수를 방지
     */

    public static String WUMPUS = "Wumpus";
    public static String BAT = "Bat";
    public static String PIT = "Pit";
    public static String NOTHING = "Nothing";

    /*
    맵에 위치시킬 위험요소들을 나타내는 ArrayList
    hazards의 1번째 값은 1번째 방에 위치한 위험요소를 의미
    ArrayList는 생성과 동시에 값을 추가할 수 없으므로
    main 메소드에서 값을 추가해줄 것
     */
    public static ArrayList<String> hazards = new ArrayList<>();

    /*
    위험요소가 근처에 있을 때 출력해줄 메시지를 정의한 HashMap
    키는 위험요소의 이름이고 값은 출력해줄 메시지
    HashMap은 생성과 동시에 값을 추가할 수 없으므로
    main 메소드에서 값을 추가해줄 것
     */
    public static HashMap<String, String> hazardMessages = new HashMap<>();

    /*
    매 게임이 동일하게 진행되지 않도록 난수 생성을 담당한 Random 객체
    난수를 이용해 위험요소의 위치, 플레이어의 시작 위치 등을
    랜덤하게 설정
     */
    public static Random random = new Random();

    public static Scanner scanner = new Scanner(System.in);

    /*
    반복문에서 게임이 플레이 중인지 종료되었는지 여부를 확인하기 위한 변수
    플레이가 시작되면 이 값을 false로 변경
    플레이에서 목적을 달성하거나 실패하는 등의 경우 true로 변경
     */

    public static boolean gameOver = true;

    // 플레이어가 현재 위치한 방의 번호를 나타내는 변수
    public static int currentRoom;

    // 플레이어가 현재 가진 화살의 수를 나타내는 변수
    public static int arrowCount;

    // 움퍼스(Wumpus)가 현재 위치한 방의 번호를 나타내는 변수
    public static int wumpusRoom;

    public static void main(String[] args) {
        showIntro();

        // 게임이 내내 변하지 않는 값들을 먼저 초기화
        initializeStaticValues();

        /*
        게임 전체를 감싸는 반복문
        이 반복문은 게임 플레이가 시작되고 목적을 달성하거나 실패함으로 인해서
        게임이 종료될때까지의 구간을 1회로 정의
         */
        while (true) {
            // 게임 플레이가 시작되기 전 플레이와 관련된 변수들을 초기화
            initializePlayVariables();

            // 각 방에 랜덤하게 위험요소 배치
            setupHazards();

            delay(1000L);
            System.out.println("\n...");
            delay(1000L);
            System.out.println("...");
            delay(1000L);
            System.out.println("동굴에 들어왔습니다...\n");
            delay(1000L);
            System.out.println("\"섬뜩한 곳이군.\"");
            delay(1000L);

            // 게임 플레이 시작
            game();
        }
    }

    /*
    게임 플레이의 메인 반복문
    여기에서는 반복해서 플레이어의 행동을 입력받고 그에 따른 처리를 함
     */
    private static void game() {
        // 이제 gameOver가 false인 동안 플레이를 반복
        while (gameOver == false) {
            // 플레이어가 취할 수 있는 행동의 목록 안내
            System.out.println("\n당신은" + currentRoom + "번 방에 있습니다.");
            System.out.println("행동을 선택하세요.");
            System.out.println("1. 이동");
            System.out.println("2. 화살쏘기");
            System.out.println("3. 통로 목록");
            System.out.println("0. 플레이 종료");

            String action = scanner.nextLine();

            /*
            이동을 선택한 경우 연결된 방 목록을 알려주고
            이동할 방 번호를 입력받아 move 메소드로 이동 처리
             */
            if (action.equals("1")) {
                // 현재 방에 연결된 방 목록 가져옴
                Integer[] nextRooms = links[currentRoom];

                // 연결된 방 목록 출력
                System.out.println("\n몇번 방으로 이동하시겠습니까?");
                System.out.println(Arrays.toString(nextRooms));

                // 이동할 방 번호를 입력받아 정수로 변환
                String nextRoomInput = scanner.nextLine();
                int nextRoom = parseIntegerOrNegative1(nextRoomInput);

                /*
                입력받은 방 번호가 연결된 방 목록에 있는지를 검증하고
                이동 처리를 하거나 이동 불가 메시지를 출력
                 */
                if (Arrays.asList(nextRooms).contains(nextRoom)) {
                    move(nextRoom);
                } else {
                    System.out.println("선택한 방으로는 이동할 수 없습니다.");
                }
            }

            /*
            화살쏘기를 선택한 경우 연결된 방 목록을 알려주고
            화살을 쏠 방 번호를 입력받아 shoot 메소드로 화살쏘기 처리
             */
            else if (action.equals("2")) {
                // 현재 방에 연결된 방 목록을 가져옴
                Integer[] nextRooms = links[currentRoom];

                // 연결된 방 목록 출력
                System.out.println("\n몇번 방에 화살을 쏘시겠습니까?");
                System.out.println(Arrays.toString(nextRooms));

                // 화살을 쏠 방 번호를 입력받아 정수로 변환
                String targetRoomInput = scanner.nextLine();
                int targetRoom = parseIntegerOrNegative1(targetRoomInput);

                /*
                입력받은 방 번호가 연결된 방 목록에 있는지를 검증하고
                화살쏘기 처리를 하거나 화살쏘기 불가 메시지 출력
                 */
                if (Arrays.asList(nextRooms).contains(targetRoom)) {
                    shoot(targetRoom);
                } else {
                    System.out.println("선택한 방에는 화살을 쏠 수 없습니다.");
                }
            }

            // 현재 방에서 연결된 통로의 목록 출력
            else if (action.equals("3")) {
                System.out.println("현재 방에 연결된 통로는 다음과 같습니다.");
                System.out.println(Arrays.asList(links[currentRoom]));
            }

            /*
            플레이 종료를 선택한 경우 gameOver 값을 변경해
            게임 플레이를 종료
             */
            else if (action.equals("0")) {
                System.out.println("게임 플레이를 종료합니다.");
                gameOver = true;
            }

            /*
            앞에서 처리한 숫자 이외의 값을 입력한 경우
            잘못된 입력이라는 메시지를 출력
             */
            else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    /*
    플레이어가 화살을 쐈을 때 그 이벤트들을 처리하는 메소드
    화살을 쏜 방에 움퍼스가 있는지를 판단하고
    움퍼스를 맞췄을 때와 그렇지 않을 때의 처리
     */
    private static void shoot(int Room) {
        // 화살의 개수를 하나 줄임
        arrowCount--;
    }

    private static void move(int room) {
        // 먼저 플레이어의 현재 위치를 이동할 방으로 변경
        currentRoom = room;
        System.out.println(currentRoom + "번 방으로 이동했습니다.");

        String hazard = hazards.get(currentRoom);

        delay(1000L);

        /*
        만일 이동한 방에 움퍼스가 있다면
        움퍼스가 플레이어를 잡아먹고 게임 플레이 종료
         */
        if (hazard.equals(WUMPUS)) {
            System.out.println("\"으아아아아악!\"");
            delay(300L);
            System.out.println("움퍼스가 당신을 잡아먹었습니다");
            gameOver = true;
        }

        else if (hazard.equals(PIT)) {
            System.out.println("\"으아아아아아아아-\"");
            delay(1000L);
            System.out.println("쿵!");
            delay(300L);
            System.out.println("당신은 구덩이에 빠졌습니다.");
            delay(300L);
            System.out.println("더이상 움퍼스를 사냥할 수 없습니다.");
            gameOver = true;
        }

        /*
        만일 이동해간 방에 박쥐가 있다면
        박쥐가 플레이어를 잡아 다른 방에 던짐
        박쥐가 플레이어를 잡아 옮길 때에는 다른 박쥐가 있는 방은 피함
        플레이어를 던져버린 박쥐는 또 다른 방으로 이동
        박쥐 또한 또다른 박쥐가 있는 방을 피해서 이동
         */
        else if (hazard.equals(BAT)) {
            System.out.println("쿵!");
            delay(300L);
            System.out.println("박쥐가 당신을 잡아 다른 방에 떨어트렸습니다.");

            /*
            플레이어가 이동할 방을 랜덤하게 선택
            만일 선택한 방에 박쥐가 있다면 박쥐가 없는 방이 나올때까지
            랜덤한 방을 다시 선택
             */
            do {
               currentRoom = random.nextInt(rooms.length);
            } while (hazards.get(currentRoom).equals(BAT));

            /*
            박쥐를 이동시키기 위해 원해 방의 박쥐는 먼저 제거
            플레이어가 이동할 방을 선택하는 것보다 박쥐를 먼저 제거하면
            플레이어가 제자리에 머무는 경우가 생기게 되므로
            플레이어가 이동할 위치를 먼저 선택한 후 박쥐를 제거
             */
            hazards.set(room, NOTHING);

            /*
            플레이어를 이동시킨 후에는 플레이어가 있는 방이나
            또다른 박쥐가 있는 방을 피해 박쥐를 이동
             */
            while (true) {
                // 박쥐가 이동해갈 방을 랜덤하게 선택
                int newBatRoom = random.nextInt(rooms.length);

                /*
                선택된 방이 플레이어가 있는 방이라면
                반복문의 청므으로 되돌아가 방을 다시 선택
                 */
                if (newBatRoom == currentRoom) {
                    continue;
                }
            }
        }
    }

    /*
    문자열을 정수로 변환해 반환
     */
    private static int parseIntegerOrNegative1(String input) {
        return Integer.parseInt(input);
    }

    private static void setupHazards() {
        /*
        게임이 시작되었을 때 위험요소를 나타내는 목록이 비어있다면
        목록을 "Nothing"으로 초기화
         */
        if (hazards.size() == 0) {
            for (int i = 0; i < rooms.length; i++) {
                hazards.add(NOTHING);
            }
        }

        /*
        게임이 다시 플레이할 때를 위해 각 방에 위치한 위험요소들을 제거
        (Nothing으로 설정)
         */
        for (int i = 0; i < rooms.length; i++) {
            hazards.set(i, NOTHING);
        }

        /*
        위험요소를 배치할 순서를 나타내는 배열
        이 순서에 따르면 움퍼스를 처음 배치하게 되는데 게임의 규칙 상
        움퍼스는 한마리뿐이므로 배열에는 Wumpus가 한번만 등장
        마찬가지로 박쥐는 세마리를 배치하고 구덩이는 두개를 배치
         */
        String[] ordinals = {WUMPUS, BAT, BAT, BAT, PIT, PIT};

        // 앞서 정의한 순서대로 위험요소 배치 반복
        for (String hazard : ordinals) {
            int room;

            /*
            이번 순서에 해당하는 위험요소를 배치할 방을 랜덤하게 선택
            위험요소를 배치할 때에는 다음 규칙을 따름
            1. 랜덤하게 선택한 방이 게임 시작 시 플레이어의 위치와 같거나
            그 방과 연결 된 방인 경우 배치하지 않음
            2. 현재 방에 이미 배치된 위험요소가 있는 경우 다른 방을 선택
             */
            while (true) {
                /*
                방들의 번호 내에서 랜덤하게 숫자 하나를 선택
                rooms.length는 20이므로 선택된 수는 0~19 중 하나
                 */
                room = random.nextInt(rooms.length);

                /*
                만일 플레이어가 위치한 방과 같거나 연결된 방이라면
                반복문의 청므으로 돌아가 랜덤한 방 번호를 다시 선택
                 */
                if (isTooCloseWithPlayer(room)) {
                    continue;
                }
            }
        }
    }

    /*
    위험요소를 배치할 방을 정할 때
    방이 플레이어의 위치와 너무 가까운지를 판단하는 메소드
    다음과 같은 경우 너무 가깝다는 의미의 true를 반환

    1. 선택한 방이 플레이어가 현재 위치한 방인 경우(currentRoom == room)
    2. 그 방과 연결된 방인 경우(links[currentRoom]에 room이 포함된 경우)

    반환값이 true이면 플레이어와 너무 가까우므로 위험요소를 배치할 수 없음
    반환값이 false이면 플레이어와 충분히 멀어서 위험요소를 배치할 수 있음
     */
    private static boolean isTooCloseWithPlayer(int room) {
        // 플레이어가 위치한 방일 경우를 판단
        if (currentRoom == room) {
            return true;
        }

        /*
        플레이어가 위치한 방과 연결된 방일 경우를 판단
        links[currentRoom] 내의 요소들을 반복문을 이용해 비교하는 방법도 있지만
        컬렉션의 contains 메소드를 사용하면 조금 더 쉽게 판단할 수 있음
         */
        List<Integer> linkedRooms = Arrays.asList(links[currentRoom]);
        if (linkedRooms.contains(room)) {
            return true;
        }

        /*
        앞에서 판단한 경우들에 해당하지 않으면
        플레이어의 위치와 가깝지 않다는 의미의 false를 반환
         */
        return false;
    }

    private static void initializePlayVariables() {
        // 게임이 시작되면 gameOver 변수를 false로 설정
        gameOver = false;

        currentRoom = random.nextInt(rooms.length);
        arrowCount = 5;
    }

    // 게임이 내내 변하지 않는 값들을 먼저 초기화해주는 메소드
    // 위험요소 근처에서 출력해줄 메시지가 이에 해당
    private static void initializeStaticValues() {
        /*
        각 위험요소가 근처에 있을 때 출력해줄 메시지
        예를 들어, 구덩이(Pit)이 근처에 있다면
        "바람이 부는 소리가 들리는 것 같다."라는 메시지를 출력
         */
        hazardMessages.put(WUMPUS, "\"어디선가 끔찍한 냄새가 난다.\"");
        hazardMessages.put(BAT, "\"어디선가 부스럭거리는 소리가 들린다.\"");
        hazardMessages.put(BAT, "\"바람이 부는 소리가 들리는 것 같다.\"");
        hazardMessages.put(BAT, "\"저 방에는 아무것도 없는 것 같다.\"");
    }

    /*
    src/intro.txt 파일을 읽어 인트로 출력
    한줄 출력할때마다 500ms씩 쉼
     */
    private static void showIntro() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/intro.txt");
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
                delay(500L);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void delay(Long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
