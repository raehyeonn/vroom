import { Navigate } from "react-router-dom";

const PrivateRoute = ({children}) => {
    const accessToken = localStorage.getItem('accessToken');

    if(!accessToken) {
        alert("로그인이 필요합니다.");
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default PrivateRoute;