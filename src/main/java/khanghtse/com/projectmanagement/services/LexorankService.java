package khanghtse.com.projectmanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LexorankService implements ILexorankService{

    private static final String MIN_CHAR = "0";
    private static final String MAX_CHAR = "z";
    private static final String INITIAL_MIN_RANK = "0|00000"; // Mốc bắt đầu
    private static final String INITIAL_MAX_RANK = "0|zzzzz"; // Mốc kết thúc

    @Override
    public String getRankBetween(String prev, String next) {
        if (prev == null) prev = INITIAL_MIN_RANK;
        if (next == null) next = INITIAL_MAX_RANK;

        // Xử lý chuỗi format "bucket|rank" (VD: 0|aaaaa)
        String pRank = prev.contains("|") ? prev.split("\\|")[1] : prev;
        String nRank = next.contains("|") ? next.split("\\|")[1] : next;

        String mid = findMidString(pRank, nRank);
        return "0|" + mid; // Giả sử dùng bucket 0
    }

    private String findMidString(String prev, String next) {
        int i = 0;
        StringBuilder mid = new StringBuilder();

        while (true) {
            char pChar = i < prev.length() ? prev.charAt(i) : 'a'; // 'a' là giá trị thấp (sau 0-9)
            char nChar = i < next.length() ? next.charAt(i) : '{'; // '{' là ký tự sau 'z' trong ASCII

            if (pChar == nChar) {
                mid.append(pChar);
                i++;
                continue;
            }

            int diff = nChar - pChar;
            if (diff > 1) {
                // Nếu có khoảng trống, lấy ký tự ở giữa
                // VD: giữa 'a' và 'c' là 'b'
                mid.append((char) (pChar + diff / 2));
                return mid.toString();
            } else {
                // Nếu liền kề (VD: 'a' và 'b'), cần tăng độ dài chuỗi
                // VD: giữa "a" và "b" -> "an" (giữa a.. và b..)
                mid.append(pChar);
                // Đệ quy logic ở cấp tiếp theo: tìm giữa (min) và (max)
                // Ở đây đơn giản hóa: append 'n' (khoảng giữa a-z)
                mid.append('n');
                return mid.toString();
            }
        }
    }
}
