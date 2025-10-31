'use client';

interface SearchAndFilterProps {
    searchTerm: string;
    onSearchChange: (term: string) => void;
    selectedCategory: string;
    onCategoryChange: (category: string) => void;
}

const categories = ['Все', 'Техника', 'Мебель', 'Одежда', 'Недвижимость', 'Авто', 'Другое'];

export function SearchAndFilter({
                                    searchTerm,
                                    onSearchChange,
                                    selectedCategory,
                                    onCategoryChange
                                }: SearchAndFilterProps) {
    return (
        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
            <div className="flex flex-col md:flex-row gap-4">
                {/* Поиск */}
                <div className="flex-1">
                    <input
                        type="text"
                        placeholder="Поиск объявлений..."
                        value={searchTerm}
                        onChange={(e) => onSearchChange(e.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                {/* Фильтр по категориям */}
                <div className="flex gap-2 overflow-x-auto pb-2 md:pb-0">
                    {categories.map(category => (
                        <button
                            key={category}
                            onClick={() => onCategoryChange(category === 'Все' ? '' : category)}
                            className={`px-4 py-2 rounded-full whitespace-nowrap transition-colors ${
                                selectedCategory === (category === 'Все' ? '' : category)
                                    ? 'bg-blue-600 text-white'
                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            }`}
                        >
                            {category}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
}