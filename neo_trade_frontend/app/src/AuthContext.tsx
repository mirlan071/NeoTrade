'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, LoginRequest, RegisterRequest, apiClient } from '@/app/src/lib/api';

interface AuthContextType {
    user: User | null;
    login: (credentials: LoginRequest) => Promise<void>;
    register: (userData: RegisterRequest) => Promise<void>;
    registerSeller: (userData: RegisterRequest) => Promise<void>;
    logout: () => void;
    isAuthenticated: boolean;
    loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Проверяем авторизацию при загрузке
        const token = localStorage.getItem('authToken');
        if (token) {
            // Здесь можно добавить запрос для получения данных пользователя
            // пока просто устанавливаем, что пользователь авторизован
            setUser({} as User); // Заглушка
        }
        setLoading(false);
    }, []);

    const login = async (credentials: LoginRequest) => {
        const response = await apiClient.login(credentials);
        setUser(response.user);
    };

    const register = async (userData: RegisterRequest) => {
        const response = await apiClient.register(userData);
        setUser(response.user);
    };

    const registerSeller = async (userData: RegisterRequest) => {
        const response = await apiClient.registerSeller(userData);
        setUser(response.user);
    };

    const logout = () => {
        apiClient.logout();
        setUser(null);
    };

    const value = {
        user,
        login,
        register,
        registerSeller,
        logout,
        isAuthenticated: !!user,
        loading,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}