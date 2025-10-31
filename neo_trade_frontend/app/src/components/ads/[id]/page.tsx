'use client';

import { useParams } from 'next/navigation';
import { Ad } from '@/app/src/components/ads/AdCard';

// Временные данные (позже заменим на бэкенд)
const mockAds: Ad[] = [
    {
        id: '1',
        title: 'iPhone 15 Pro',
        description: 'Новый iPhone в идеальном состоянии. Использовался 2 месяца. Все чеки и коробка в наличии. Батарея 100%. Цвет натуральный титан.',
        price: 450000,
        category: 'Техника',
        image: 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=500'
    },
    {
        id: '2',
        title: 'Диван угловой',
        description: 'Угловой диван, почти новый. Цвет серый. Размеры: 280x180 см. Материал: экокожа. Мягкий и удобный, идеальное состояние.',
        price: 120000,
        category: 'Мебель',
        image: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500'
    }
];

export default function AdDetailPage() {
    const params = useParams();
    const adId = params.id as string;

    const ad = mockAds.find(a => a.id === adId);

    if (!ad) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <h1 className="text-2xl font-bold text-gray-900 mb-4">Объявление не найдено</h1>
                    <a href="/" className="text-blue-600 hover:text-blue-700">Вернуться на главную</a>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-8">
                <a href="/" className="text-blue-600 hover:text-blue-700 mb-6 inline-block">
                    ← Назад к объявлениям
                </a>

                <div className="bg-white rounded-lg shadow-lg overflow-hidden">
                    {ad.image && (
                        <img
                            src={ad.image}
                            alt={ad.title}
                            className="w-full h-96 object-cover"
                        />
                    )}

                    <div className="p-6">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h1 className="text-3xl font-bold text-gray-900 mb-2">{ad.title}</h1>
                                <span className="inline-block bg-gray-100 text-gray-600 px-3 py-1 rounded-full text-sm">
                  {ad.category}
                </span>
                            </div>
                            <div className="text-right">
                                <div className="text-3xl font-bold text-green-600">{ad.price.toLocaleString()} ₸</div>
                                <button className="mt-3 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors">
                                    Написать продавцу
                                </button>
                            </div>
                        </div>

                        <div className="prose max-w-none">
                            <h3 className="text-xl font-semibold mb-3">Описание</h3>
                            <p className="text-gray-700 leading-relaxed">{ad.description}</p>
                        </div>

                        <div className="mt-8 pt-6 border-t border-gray-200">
                            <h3 className="text-lg font-semibold mb-3">Информация о продавце</h3>
                            <div className="flex items-center gap-3">
                                <div className="w-12 h-12 bg-gray-300 rounded-full flex items-center justify-center">
                                    <span className="text-gray-600 font-semibold">А</span>
                                </div>
                                <div>
                                    <p className="font-semibold">Айбек</p>
                                    <p className="text-sm text-gray-500">На сайте 2 месяца</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}