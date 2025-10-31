const API_BASE_URL = 'http://localhost:8080/api'; // Ваш Spring Boot порт

export interface User {
    id: string;
    phoneNumber: string;
    firstName: string;
    lastName: string;
    email?: string;
    role: 'USER' | 'SELLER' | 'ADMIN';
    profileImageUrl?: string;
}

export interface Ad {
    id: string;
    title: string;
    description: string;
    price: number;
    category: 'ELECTRONICS' | 'CARS' | 'REAL_ESTATE' | 'JOBS' | 'SERVICES' | 'OTHER';
    region: string;
    owner: User;
    images?: string[];
    createdAt: string;
}

export interface LoginRequest {
    phoneNumber: string;
    password: string;
}

export interface RegisterRequest {
    phoneNumber: string;
    password: string;
    firstName: string;
    lastName: string;
    email?: string;
}

export interface AuthResponse {
    token: string;
    user: User;
}

class ApiClient {
    private baseUrl: string;
    private token: string | null = null;

    constructor(baseUrl: string) {
        this.baseUrl = baseUrl;
        if (typeof window !== 'undefined') {
            this.token = localStorage.getItem('authToken');
        }
    }

    private async request(endpoint: string, options: RequestInit = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
            'Authorization': 'aaa',
            ...options.headers,
        };

        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        const config: RequestInit = {
            ...options,
            headers,
        };

        const response = await fetch(url, config);

        if (!response.ok) {
            throw new Error(`API error: ${response.status}`);
        }

        return response.json();
    }

    // Аутентификация
    async login(credentials: LoginRequest): Promise<AuthResponse> {
        const result = await this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials),
        });

        this.token = result.token;
        if (typeof window !== 'undefined') {
            localStorage.setItem('authToken', result.token);
        }

        return result;
    }

    async register(userData: RegisterRequest): Promise<AuthResponse> {
        const result = await this.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData),
        });

        this.token = result.token;
        if (typeof window !== 'undefined') {
            localStorage.setItem('authToken', result.token);
        }

        return result;
    }

    async registerSeller(userData: RegisterRequest): Promise<AuthResponse> {
        const result = await this.request('/auth/register/seller', {
            method: 'POST',
            body: JSON.stringify(userData),
        });

        this.token = result.token;
        if (typeof window !== 'undefined') {
            localStorage.setItem('authToken', result.token);
        }

        return result;
    }

    // Объявления
    async getAds(): Promise<Ad[]> {
        return this.request('/ads');
    }

    async searchAds(params: {
        category?: string;
        region?: string;
        search?: string;
        minPrice?: number;
        maxPrice?: number;
    }): Promise<Ad[]> {
        const queryParams = new URLSearchParams();
        Object.entries(params).forEach(([key, value]) => {
            if (value !== undefined && value !== '') {
                queryParams.append(key, value.toString());
            }
        });

        return this.request(`/ads/search?${queryParams}`);
    }

    async createAd(adData: Omit<Ad, 'id' | 'owner' | 'createdAt'>): Promise<Ad> {
        return this.request('/ads', {
            method: 'POST',
            body: JSON.stringify(adData),
        });
    }

    async getAdById(id: string): Promise<Ad> {
        return this.request(`/ads/${id}`);
    }

    // Выход
    logout() {
        this.token = null;
        if (typeof window !== 'undefined') {
            localStorage.removeItem('authToken');
        }
    }

    // Проверка авторизации
    isAuthenticated(): boolean {
        return !!this.token;
    }
}

export const apiClient = new ApiClient(API_BASE_URL);