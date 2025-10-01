# Lab 17: NLP Pipeline with Spark

## Nguyễn Đức Đạt - 23000109

---

### Nội dung chính:

- Đọc file `c4-train.00000-of-01024-30K.json.gz` bằng
  `spark.read.json()` thành công, load được 2000 rows.

- Sử dụng `Pipeline` từ `org.apache.spark.ml`.

- Sử dụng `RegexTokenizer` để tách từ từ cột văn bản.

- Áp dụng `StopWordsRemover` với danh sách stopwords mặc định của
  Spark.

- Dùng `HashingTF` để chuyển tokens thành vector tần suất, sau đó
  dùng `IDF` để chuẩn hóa.

- Gọi `pipeline.fit(df).transform(df)` để train và transform thành công, kết quả chứa
  vector đặc trưng.

- Ghi output vào `results/lab17_pipeline_output.txt`.

- Spark log tự động sinh ra. Ngoài ra, chương trình có thêm log:

  [info] Loaded 2000 rows
  [info] Pipeline completed. Results saved at results/lab17_pipeline_output.txt

---

### Các bước trong chương trình

1.  Đặt file dữ liệu vào thư mục `data/`.
2.  Đọc dữ liệu bằng `spark.read.json`.
3.  Tạo `RegexTokenizer` để tách tokens.
4.  Dùng `StopWordsRemover` để loại bỏ stop words.
5.  Dùng `HashingTF` và `IDF` để vector hóa dữ liệu.
6.  Tạo `Pipeline` gồm các bước trên.
7.  Fit & transform dữ liệu.
8.  Ghi kết quả ra file `results/lab17_pipeline_output.txt` và log vào file log.

### Cấu trúc thư mục

spark_labs/
├── data/ # chứa dữ liệu đầu vào (vd: C4 dataset)
│ └── c4-train.00000-of-01024-30K.json.gz
│
├── log/ # thư mục chứa log
│ └── lab17_log.txt
│
├── project/ # file cấu hình của sbt project
│ ├── target/
│ └── build.properties
│
├── results/ # kết quả chạy pipeline
│ └── lab17_pipeline_output.txt
│
├── src/
│ └── main/
│ └── scala/
│ └── com/
│ └── donkihote/
│ └── spark/
│ └── Lab17_NLPPipeline.scala
│
├── target/ # thư mục build output tự động tạo bởi sbt
│ ├── bg-jobs/
│ ├── global-logging/
│ ├── scala-2.12/
│ ├── streams/
│ └── task-temp-directory/
│
├── build.sbt # file cấu hình chính cho project
└── report_lab17.md # file báo cáo bài lab

### Cách chạy code: tại thư mục root

```bash
cd spark_labs
sbt clean compile
sbt run
```

→ Log chạy sẽ xuất hiện trong console. Kết quả được lưu trong
`results/lab17_pipeline_output.txt`.

### Giải thích

- Spark load được 2000 dòng dữ liệu từ C4.\
- Sau khi pipeline chạy, mỗi văn bản được token hóa, loại bỏ
  stopwords, chuyển thành vector TF-IDF.\
- Kết quả cuối cùng là ma trận đặc trưng TF-IDF của dữ liệu, dùng
  cho các bước học máy tiếp theo.

### 1 số khó khăn

- **Vấn đề 1:** Cảnh báo `winutils.exe` trên Windows.\
  → Đây chỉ là cảnh báo, bỏ qua được khi chạy Spark
  standalone.\
- **Vấn đề 2:** Java version cao (17+).Do máy e là java 24 nên sẽ bị lỗi \
  → Thêm `--add-opens` vào `build.sbt` để tránh lỗi
  IllegalAccess.

### Nguồn tham khảo

- Tài liệu Spark ML:
  https://spark.apache.org/docs/3.5.1/ml-guide.html\
- Spark trên Windows (winutils.exe):
  https://wiki.apache.org/hadoop/WindowsProblems
- ChatGPT

### Mô hình tiền huấn luyện

       → Không sử dụng pre-trained models trong lab này.

---
