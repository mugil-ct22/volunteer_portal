import React from 'react';
import './DashboardCard.css';

const DashboardCard = ({ title, value, icon, color = 'blue' }) => {
  const getCardClass = () => {
    return `dashboard-card dashboard-card--${color}`;
  };

  return (
    <div className={getCardClass()}>
      <div className="dashboard-card__content">
        <div className="dashboard-card__icon">
          {icon}
        </div>
        <div className="dashboard-card__info">
          <h3 className="dashboard-card__title">{title}</h3>
          <p className="dashboard-card__value">{value}</p>
        </div>
      </div>
    </div>
  );
};

export default DashboardCard;
