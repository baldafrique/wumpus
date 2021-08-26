import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class MoveAndSituation {

    public static int[] rooms = {0, 1, 2, 3}; // 방

    // 각 방에서 이동할 수 있는 방들의 목록을 표현한 통로
    public static int[][] links = {{1, 2, 3}, {2, 3, 0}, {3, 0, 1}, {0, 1, 2}};

    public static String BAT = "Bat";
    public static String PIT = "Pit";
    public static String WUMPUS = "Wumpus";
    public static String NOTHING = "Nothing";

    public static String[] hazards = {NOTHING, BAT, PIT, WUMPUS};

    public static int currentRooom = 0; // 현재 방 번호

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("지금 " + currentRooom + "번 방에 있습니다.");

            // 현재 방에서 갈 수 있는 방들의 목록
            int[] nextRooms = links[currentRooom];

            // 현재 방에서의 통로들을 출력
            System.out.println("다음 번호 중에서 이동할 방 번호를 입력해주세요.");
            System.out.println(Arrays.toString(nextRooms));

            // 이동해갈 방 번호를 입력받음
            int roomNumber = scanner.nextInt();

            // 플레이어 이동 처리
            move(roomNumber);
        }
    }

    private static void move(int room) {
        currentRooom = room;

        String hazardInRoom = hazards[currentRooom];
        if (hazardInRoom.equals(WUMPUS)) {
            System.out.println("움퍼스에게 잡아먹혔습니다.");
        }

        else if (hazardInRoom.equals(PIT)) {
            System.out.println("구덩이에 빠졌습니다.");
        }

        else if (hazardInRoom.equals(BAT)) {
            System.out.println("박쥐가 당신을 잡아 다른 방에 떨어트렸습니다.");
            Random random = new Random();

            // 박쥐가 없는 방이 나올때까지 랜덤하게 방을 선택
            do {
                currentRooom = random.nextInt(rooms.length);
            } while (hazards[currentRooom].equals(BAT));

            /*
            박쥐를 이동시키기 위해 원래 방의 박쥐는 먼저 제거
            플레이어가 이동할 방을 선택하는 것보다 박쥐를 먼저 제거 시
            플레이어가 제자리에 머무는 경우 발생
            플레이어가 이동할 위치를 먼저 선택 후 박쥐 제거
            */
            hazards[room] = NOTHING;

            /*
            플레이어를 이동시킨 후에는 플레이어가 있는 방이나
            또다른 박쥐가 있는 방을 피해 박쥐 이동
            */
            while (true) {
                //박쥐가 이동해 갈 방을 랜덤하게 선택
                int newBatRoom = random.nextInt(rooms.length);

                /*
                선택된 방이 플레이어가 있는 방이라면
                반복문의 처음으로 되돌아가 방을 다시 선택
                */
                if (newBatRoom == currentRooom) {
                    continue;
                }

                /*
                선택된 방에 플레이어도 또다른 위험요소도 없다면
                선택된 방에 박쥐 배치
                */
                if (hazards[newBatRoom].equals(NOTHING)) {
                    hazards[newBatRoom] = BAT;
                    break;
                }
            }

            /*
            플레이어의 위치가 변경되었으므로 다시한번
            해당 방으로 이동했을 때에 대한 이벤트 처리
             */
            move(currentRooom);
        }
    }
}
