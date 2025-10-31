'use client';

import { useState, useEffect } from 'react';
import { AdCard, Ad } from '@/app/src/components/ads/AdCard';
import { AddAdForm } from '@/app/src/components/ads/AddAdForm';
import { Header } from '@/app/src/components/layout/Header';
import { SearchAndFilter } from '@/app/src/components/ads/SearchAndFilter';
import { apiClient } from '@/app/src/lib/api';

export default function Home() {
    const [ads, setAds] = useState<Ad[]>([]);
    const [showForm, setShowForm] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Загрузка объявлений с бэкенда
    useEffect(() => {
        loadAds();
    }, []);

    const loadAds = async () => {
        try {
            setLoading(true);
            const adsData = await apiClient.getAds();
            setAds(adsData);
        } catch (err) {
            setError('Ошибка при загрузке объявлений');
            console.error('Error loading ads:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleAddAd = async (newAdData: Omit<Ad, 'id' | 'owner' | 'createdAt'>) => {
        try {
            const newAd = await apiClient.createAd(newAdData);
            setAds([...ads, newAd]);
            setShowForm(false);
        } catch (err) {
            setError('Ошибка при создании объявления');
            console.error('Error creating ad:', err);
        }
    };

    const handleSearch = async () => {
        try {
            const searchParams: any = {};
            if (searchTerm) searchParams.search = searchTerm;
            if (selectedCategory) searchParams.category = selectedCategory;

            const searchResults = await apiClient.searchAds(searchParams);
            setAds(searchResults);
        } catch (err) {
            setError('Ошибка при поиске');
            console.error('Error searching ads:', err);
        }
    };

    // Фильтрация на клиенте (или можно использовать бэкенд поиск)
    const filteredAds = ads.filter(ad => {
        const matchesSearch = ad.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            ad.description.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCategory = !selectedCategory || ad.category === selectedCategory;

        return matchesSearch && matchesCategory;
    });

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Загрузка объявлений...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <Header />

            <div className="container mx-auto px-4 py-8">
                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                        {error}
                        <button
                            onClick={() => setError(null)}
                            className="float-right font-bold"
                        >
                            ×
                        </button>
                    </div>
                )}

                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-gray-900">Объявления NeoTrade</h1>
                    <button
                        onClick={() => setShowForm(!showForm)}
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        {showForm ? 'Отмена' : '+ Добавить объявление'}
                    </button>
                </div>

                <SearchAndFilter
                    searchTerm={searchTerm}
                    onSearchChange={setSearchTerm}
                    selectedCategory={selectedCategory}
                    onCategoryChange={setSelectedCategory}
                    onSearch={handleSearch}
                />

                {showForm && (
                    <div className="mb-8">
                        <AddAdForm onAddAd={handleAddAd} />
                    </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredAds.map(ad => (
                        <AdCard key={ad.id} ad={ad} />
                    ))}
                </div>

                {filteredAds.length === 0 && !loading && (
                    <div className="text-center py-12">
                        <p className="text-gray-500 text-lg">
                            {ads.length === 0 ? 'Объявлений пока нет' : 'Ничего не найдено'}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}