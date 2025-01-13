package kakao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//2024 KAKAO WINTER INTERNSHIP 가장 많이 받은 선물
public class MostReceivedGifts {
    public static void main(String[] args) {
        int solution = solution(new String[]{"muzi", "ryan", "frodo", "neo"}, new String[]{"muzi frodo", "muzi frodo", "ryan muzi", "ryan muzi", "ryan muzi", "frodo muzi", "frodo ryan", "neo muzi"});
        System.out.println("solution1 = " + solution);

        solution = solution2(new String[]{"muzi", "ryan", "frodo", "neo"}, new String[]{"muzi frodo", "muzi frodo", "ryan muzi", "ryan muzi", "ryan muzi", "frodo muzi", "frodo ryan", "neo muzi"});
        System.out.println("solution2 = " + solution);
    }

    //다른 풀이 방법
    public static int solution2(String[] friends, String[] gifts) {
        int answer = 0;
        int totalFriends = friends.length;

        Map<String, Integer> kakaoFriends = new HashMap<>(); //name, index
        for (int i = 0; i < totalFriends; i++) {
            kakaoFriends.put(friends[i], i);
        }

        //지수 기록 배열(보낸 선물 수 - 받은 선물 수)
        int[] index = new int[totalFriends];
        //선물 내역 기록 배열
        int[][] record = new int[totalFriends][totalFriends];

        //지수 구하기
        for (String gift : gifts) {
            String[] split = gift.split(" ");
            String sender = split[0];
            String receiver = split[1];
            index[kakaoFriends.get(sender)]++; //선물 보냄
            index[kakaoFriends.get(receiver)]--; //선물 받음
            record[kakaoFriends.get(sender)][kakaoFriends.get(receiver)]++;
        }

        for (int i = 0; i < totalFriends; i++) {
            int cnt = 0;
            for (int j = 0; j < totalFriends; j++) {
                if(i == j) continue;
                if(record[i][j] > record[j][i] || (record[i][j] == record[j][i] && index[i] > index[j])){
                    cnt++;
                }
            }
            answer = Math.max(answer, cnt);
        }

        return answer;
    }

    //내 풀이 방법
    public static int solution(String[] friends, String[] gifts) {
        Map<String, List<String>> giftsSentMap = new HashMap<>(); //보낸 선물 데이터 (key:발신자)
        Map<String, List<String>> giftsReceiveMap = new HashMap<>(); //받은 선물 데이터 (key:수신자)

        for (String gift : gifts) {
            String[] splits = gift.split(" ");
            String sender = splits[0];
            String receiver = splits[1];

            giftsSentMap.computeIfAbsent(sender, e -> new ArrayList<>()).add(receiver);
            giftsReceiveMap.computeIfAbsent(receiver, e -> new ArrayList<>()).add(sender);
        }

        //친구들의 보낸 선물 수, 받은 선물 수, 지수, 선물 내역 구하기.
        List<KakaoFriend> kakaoFriendList = setKakaoFriendList(friends, giftsSentMap, giftsReceiveMap);

        // 최대 선물 수 계산
        int maxReceiveCount = 0;
        for (KakaoFriend kakaoFriend : kakaoFriendList) {
            int receiveGiftCount = 0;
            String name = kakaoFriend.name;

            for (KakaoFriend friend : kakaoFriendList) {
                if(kakaoFriend.name.equals(friend.name)) continue;

                int receiveCount = kakaoFriend.giveGiftMap.getOrDefault(friend.name, 0); //친구에게 보낸 선물 수
                int giftCount = friend.giveGiftMap.getOrDefault(name, 0); //친구에게 받은 선물 수
                if(receiveCount > giftCount ||
                        (receiveCount == giftCount && kakaoFriend.exponential > friend.exponential)) {
                    receiveGiftCount++;
                }
            }

            maxReceiveCount = Math.max(maxReceiveCount, receiveGiftCount);
        }

        return maxReceiveCount;
    }

    //친구들의 보낸 선물 수, 받은 선물 수, 지수, 선물 내역 구하기.
    public static List<KakaoFriend> setKakaoFriendList (String[] friends, Map<String, List<String>> giftsSentMap, Map<String, List<String>> giftsReceiveMap){
        List<KakaoFriend> kakaoFriendList = new ArrayList<>();
        for (String friend : friends) {
            List<String> sentGifts = giftsSentMap.getOrDefault(friend, new ArrayList<>()); //보낸 선물
            List<String> receiveGifts = giftsReceiveMap.getOrDefault(friend, new ArrayList<>()); //받은 선물

            Map<String, Integer> giveGiftMap = new HashMap<>(); //보낸 선물 정보 취합
            for (String recipient : sentGifts) {
                giveGiftMap.merge(recipient, 1, Integer::sum);
            }

            KakaoFriend kakaoFriend = new KakaoFriend(friend, sentGifts.size(), receiveGifts.size(), giveGiftMap);
            kakaoFriendList.add(kakaoFriend);
        }
        return kakaoFriendList;
    }

    public static class KakaoFriend{
        String name;
        int giveCount; //보낸 선물 수
        int receiveCount; // 받은 선물 수
        int exponential; // 지수 (보낸 선물 - 받은 선물)
        Map<String, Integer> giveGiftMap; //선물 준 내역 (받는사람, 선물 보낸 수)

        public KakaoFriend(String name, int giveCount, int receiveCount, Map<String, Integer> giveGiftMap){
            this.name = name;
            this.giveCount = giveCount;
            this.receiveCount = receiveCount;
            this.giveGiftMap = giveGiftMap;
            this.exponential = giveCount - receiveCount;
        }
    }
}