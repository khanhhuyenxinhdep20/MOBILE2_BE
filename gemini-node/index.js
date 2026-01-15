import express from "express";
import cors from "cors";
import { GoogleGenerativeAI } from "@google/generative-ai";

const app = express();
app.use(cors());
app.use(express.json());

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

app.post("/chat", async (req, res) => {
  try {
    const { message } = req.body;

    if (!message) {
      return res.status(400).json({ error: "Message is required" });
    }

    const model = genAI.getGenerativeModel({
      model: "gemini-1.5-flash"
    });

    const result = await model.generateContent(message);
    const text = result.response.text();

    res.json({ reply: text });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Gemini error" });
  }
});

app.get("/", (req, res) => {
  res.send("Gemini Node API OK");
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log("Node Gemini running on port", PORT);
});
