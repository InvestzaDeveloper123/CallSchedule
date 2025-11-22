import styles from "./PMSService.module.css";
import { useNavigate } from "react-router-dom";

const PMSService = () => {
  const navigate = useNavigate();

  const handleEdit = () => {
    navigate("/investmentDetails");
  };

  return (
    <div className={styles["pms-service-section"]}>
      <div className={styles["pms-card"]}>
        <div className={styles["pms-icon"]}>
          <svg
            width="48"
            height="48"
            viewBox="0 0 24 24"
            fill="none"
            stroke="#666"
            strokeWidth="2"
          >
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
        </div>

        <h2 className={styles["pms-title"]}>
          Unfortunately you are not eligible for Investza PMS service
        </h2>

        <p className={styles["pms-description"]}>
          SEBI Regulations require at least ₹50 Lakh to invest in PMS
        </p>

        <div className={styles["callback-card"]}>
          <div className={styles["callback-content"]}>
            <svg
              width="32"
              height="32"
              viewBox="0 0 24 24"
              fill="none"
              stroke="white"
              strokeWidth="2"
            >
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
            </svg>
            <div className={styles["callback-text"]}>
              <h3 className={styles["callback-title"]}>Need assistance?</h3>
              <p className={styles["callback-subtitle"]}>Request a callback</p>
            </div>
          </div>

          <svg
            className={styles["arrow-icon"]}
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="white"
          >
            <path d="M7 17L17 7M17 7H7M17 7V17" />
          </svg>
        </div>

        <button className={styles["edit-button"]} onClick={handleEdit}>
          Edit my current value
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6z" />
          </svg>
        </button>
      </div>
    </div>
  );
};

export default PMSService;
