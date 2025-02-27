import styles from './ArticleForm.module.css';
import { useRef, useState } from 'react';
import articlesApi from '../api/articlesApi';

const INITIAL_FORM_DATA = {
  title: '',
  content: '',
  file: null,
};

export default function ArticleForm({ fetchArticles }) {
  const [inputData, setInputData] = useState(INITIAL_FORM_DATA);
  // 파일 input 요소에 대한 참조
  const fileInputRef = useRef(null);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setInputData((prev) => ({ ...prev, [name]: value }));
  };

  const resetForm = () => {
    setInputData(INITIAL_FORM_DATA);
    // 업로드 폼을 초기화한다.
    if (fileInputRef.current) fileInputRef.current.value = null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('title', inputData.title);
    formData.append('content', inputData.content);

    if (inputData.file) {
      formData.append('file', inputData.file);
    }

    try {
      await articlesApi.postArticle(formData);
      fetchArticles();
      resetForm();
    } catch (error) {
      console.error('ERROR : ', error);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setInputData((prev) => ({ ...prev, file }));
  };

  return (
    <div className={styles.articleFormContainer}>
      <form onSubmit={handleSubmit} className={styles.form}>
        <input
          type="text"
          id="title"
          name="title"
          value={inputData.title}
          onChange={handleFormChange}
          placeholder="title 입력"
          className={styles.input}
        />
        <textarea
          id="content"
          name="content"
          value={inputData.content}
          onChange={handleFormChange}
          placeholder="content 입력"
          className={styles.textarea}
        />
        <input
          type="file"
          id="file"
          name="file"
          accept="image/*"
          onChange={handleFileChange}
        />
        <button type="submit" className={styles.button}>
          생성
        </button>
      </form>
    </div>
  );
}
