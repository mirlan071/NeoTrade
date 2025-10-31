export function Header() {
    return (
        <header className="bg-white shadow-sm border-b">
            <div className="container mx-auto px-4 py-4">
                <div className="flex justify-between items-center">
                    <h1 className="text-2xl font-bold text-blue-600">NeoTrade</h1>
                    <nav className="flex gap-6">
                        <a href="#" className="text-gray-700 hover:text-blue-600">Главная</a>
                        <a href="#" className="text-gray-700 hover:text-blue-600">Добавить объявление</a>
                        <a href="#" className="text-gray-700 hover:text-blue-600">Войти</a>
                    </nav>
                </div>
            </div>
        </header>
    );
}