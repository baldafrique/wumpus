import java.util.Arrays;
import java.util.Scanner;

public class RoomsInCave {

    public static void main(String[] args) {
        // 4개의 방
        int[] rooms = {0, 1, 2, 3};

        int[][] links = {{1, 2, 3}, {2, 3, 0}, {3, 0, 1}, {0, 1, 2}};

        int currentRoom = 0; // 현재 방 번호

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("지금 " + currentRoom + "번 방에 있습니다.");
            System.out.println("다음 번호 중에서 이동할 방 번호를 입력해주세요.");

            // 현재 방에서의 통로들 출력
            System.out.println(Arrays.toString(links[currentRoom]));

            // 이동할 방 번호 입력받음
            int roomNumber = scanner.nextInt();

            // 입력한 방으로 이동
            currentRoom = roomNumber;
        }
    }
}
